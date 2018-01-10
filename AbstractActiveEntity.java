import processing.core.PImage;
import java.util.List;

public abstract class AbstractActiveEntity extends AbstractEntity implements Active
{
    private int actionPeriod;

    public AbstractActiveEntity(Point position, List<PImage> images, String id, int actionPeriod)
    {
        super(position, images, id);
        this.actionPeriod = actionPeriod;
    }

    protected int getActionPeriod() { return actionPeriod; }

    protected void scheduleActivity(EventScheduler scheduler, WorldModel world, ImageStore imageStore, long period)
    {
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                period);
    }

    public abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    public abstract void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);
}
