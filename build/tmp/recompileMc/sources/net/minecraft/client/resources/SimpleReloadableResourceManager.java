package net.minecraft.client.resources;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class SimpleReloadableResourceManager implements IReloadableResourceManager
{
    private static final Logger logger = LogManager.getLogger();
    private static final Joiner joinerResourcePacks = Joiner.on(", ");
    private final Map<String, FallbackResourceManager> domainResourceManagers = Maps.<String, FallbackResourceManager>newHashMap();
    private final List<IResourceManagerReloadListener> reloadListeners = Lists.<IResourceManagerReloadListener>newArrayList();
    private final Set<String> setResourceDomains = Sets.<String>newLinkedHashSet();
    private final IMetadataSerializer rmMetadataSerializer;

    public SimpleReloadableResourceManager(IMetadataSerializer rmMetadataSerializerIn)
    {
        this.rmMetadataSerializer = rmMetadataSerializerIn;
    }

    public void reloadResourcePack(IResourcePack resourcePack)
    {
        for (String s : resourcePack.getResourceDomains())
        {
            this.setResourceDomains.add(s);
            FallbackResourceManager fallbackresourcemanager = (FallbackResourceManager)this.domainResourceManagers.get(s);

            if (fallbackresourcemanager == null)
            {
                fallbackresourcemanager = new FallbackResourceManager(this.rmMetadataSerializer);
                this.domainResourceManagers.put(s, fallbackresourcemanager);
            }

            fallbackresourcemanager.addResourcePack(resourcePack);
        }
    }

    public Set<String> getResourceDomains()
    {
        return this.setResourceDomains;
    }

    public IResource getResource(ResourceLocation location) throws IOException
    {
        IResourceManager iresourcemanager = (IResourceManager)this.domainResourceManagers.get(location.getResourceDomain());

        if (iresourcemanager != null)
        {
            return iresourcemanager.getResource(location);
        }
        else
        {
            throw new FileNotFoundException(location.toString());
        }
    }

    public List<IResource> getAllResources(ResourceLocation location) throws IOException
    {
        IResourceManager iresourcemanager = (IResourceManager)this.domainResourceManagers.get(location.getResourceDomain());

        if (iresourcemanager != null)
        {
            return iresourcemanager.getAllResources(location);
        }
        else
        {
            throw new FileNotFoundException(location.toString());
        }
    }

    private void clearResources()
    {
        this.domainResourceManagers.clear();
        this.setResourceDomains.clear();
    }

    public void reloadResources(List<IResourcePack> p_110541_1_)
    {
        this.clearResources();
        net.minecraftforge.fml.common.ProgressManager.ProgressBar resReload = net.minecraftforge.fml.common.ProgressManager.push("Loading Resources", p_110541_1_.size()+1, true);
        logger.info("Reloading ResourceManager: " + joinerResourcePacks.join(Iterables.transform(p_110541_1_, new Function<IResourcePack, String>()
        {
            public String apply(IResourcePack p_apply_1_)
            {
                return p_apply_1_.getPackName();
            }
        })));

        for (IResourcePack iresourcepack : p_110541_1_)
        {
            resReload.step(iresourcepack.getPackName());
            this.reloadResourcePack(iresourcepack);
        }

        resReload.step("Reloading listeners");
        this.notifyReloadListeners();
        net.minecraftforge.fml.common.ProgressManager.pop(resReload);
    }

    public void registerReloadListener(IResourceManagerReloadListener reloadListener)
    {
        net.minecraftforge.fml.common.ProgressManager.ProgressBar resReload = net.minecraftforge.fml.common.ProgressManager.push("Loading Resource", 1);
        resReload.step(reloadListener.getClass());
        this.reloadListeners.add(reloadListener);
        reloadListener.onResourceManagerReload(this);
        net.minecraftforge.fml.common.ProgressManager.pop(resReload);
    }

    private void notifyReloadListeners()
    {
        net.minecraftforge.fml.common.ProgressManager.ProgressBar resReload = net.minecraftforge.fml.common.ProgressManager.push("Reloading", this.reloadListeners.size());
        for (IResourceManagerReloadListener iresourcemanagerreloadlistener : this.reloadListeners)
        {
            resReload.step(iresourcemanagerreloadlistener.getClass());
            iresourcemanagerreloadlistener.onResourceManagerReload(this);
        }
        net.minecraftforge.fml.common.ProgressManager.pop(resReload);
    }
}