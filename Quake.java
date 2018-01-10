import processing.core.PImage;
import java.util.List;
import java.util.Random;

public class Quake extends AbstractAnimatedEntity
{
    private static final String QUAKE_ID = "quake";
    private static final int QUAKE_ACTION_PERIOD = 1100;
    private static final int QUAKE_ANIMATION_PERIOD = 100;
    private static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;

    public Quake(Point position, List<PImage> images)
    {
        super(position, images, QUAKE_ID, QUAKE_ACTION_PERIOD, QUAKE_ANIMATION_PERIOD);
    }

    protected int getRepeatCount()
    {
        return QUAKE_ANIMATION_REPEAT_COUNT;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        scheduler.unscheduleAllEvents(this);
        world.removeEntity(this);
    }

    public <R> R accept(EntityVisitor<R> visitor)
    {
        return visitor.visit(this);
    }
}
