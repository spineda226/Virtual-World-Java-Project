import processing.core.PImage;
import java.util.List;

public abstract class AbstractNotMoveableEntity extends AbstractActiveEntity
{
    public AbstractNotMoveableEntity(Point position, List<PImage> images, String id, int actionPeriod)
    {
            super(position, images, id, actionPeriod);
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
    {
        scheduleActivity(scheduler, world, imageStore, getActionPeriod());
    }
}
