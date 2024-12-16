package keystrokesmod.event;


import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import net.minecraftforge.fml.common.eventhandler.Event;

public class MoveInputEvent extends Event {
    private float forward, strafe;
    private boolean jump, sneak;
    private double sneakSlowDown;

    public MoveInputEvent(float forward, float strafe, boolean jump, boolean sneak, double sneakSlowDown) {
        this.forward = forward;
        this.strafe = strafe;
        this.jump = jump;
        this.sneak = sneak;
        this.sneakSlowDown = sneakSlowDown;
    }

    public void setForward(float forward) {
        this.forward = forward;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void setSneak(boolean sneak) {
        this.sneak = sneak;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    @Override
    public void setCanceled(boolean cancel) {
        if (cancel) {
            setForward(0);
            setStrafe(0);
            setJump(false);
            setSneak(false);
        }
    }

    public double getSneakSlowDown() {
        return sneakSlowDown;
    }

    public float getForward() {
        return forward;
    }

    public float getStrafe() {
        return strafe;
    }

    public boolean isJump() {
        return jump;
    }

    public boolean isSneak() {
        return sneak;
    }
}