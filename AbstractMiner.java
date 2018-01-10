import processing.core.PImage;
import java.util.List;

public abstract class AbstractMiner extends AbstractMoveableEntity
{
    private int resourceCount;
    private int resourceLimit;

    public AbstractMiner(Point position, List<PImage> images, String id, int actionPeriod, int animationPeriod, int resourceCount, int resourceLimit)
    {
        super(position, images, id, actionPeriod, animationPeriod);
        this.resourceCount = resourceCount;
        this.resourceLimit = resourceLimit;
    }

    protected int getResourceCount() { return resourceCount; }

    protected int getResourceLimit() { return resourceLimit; }

    protected void _applyAdjacent(WorldModel world, Entity target, EventScheduler scheduler)
    {
        if (resourceCount < resourceLimit)
        {
            resourceCount += 1;
            eliminateEntity(world, target, scheduler);
        }
    }

    protected void replaceEntity(WorldModel world, EventScheduler scheduler, ImageStore imageStore, AbstractMiner miner)
    {
        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(miner);
        miner.scheduleActions(scheduler, world, imageStore);
    }

    protected boolean _applyCanPassThrough(WorldModel world, Point p)
    {
        return world.withinBounds(p) && !world.isOccupied(p);
    }
}
