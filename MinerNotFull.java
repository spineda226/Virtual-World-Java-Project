import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class MinerNotFull extends AbstractMiner
{
    public MinerNotFull(String id, Point position,
                  List<PImage> images, int resourceLimit,
                  int actionPeriod, int animationPeriod)
    {
        super(position, images, id, actionPeriod, animationPeriod, 0, resourceLimit);
    }

    protected EntityVisitor<Boolean> getTargetEntityVisitor() { return new OreVisitor(); }

    public void _applyMoveableActivity(Optional<Entity> target, WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        if (!target.isPresent() ||
                !moveTo(world, target.get(), scheduler) ||
                !transformNotFull(world, scheduler, imageStore))
        {
            scheduleActivity(scheduler, world, imageStore, getActionPeriod());
        }
    }

    private boolean transformNotFull(WorldModel world,
                                     EventScheduler scheduler, ImageStore imageStore)
    {
        if (getResourceCount() >= getResourceLimit())
        {
            MinerFull miner = new MinerFull(getId(), getPosition(), getImages(),
                    getResourceLimit(), getActionPeriod(), getAnimationPeriod());

            replaceEntity(world, scheduler, imageStore, miner);
            return true;
        }

        return false;
    }

    public <R> R accept(EntityVisitor<R> visitor)
    {
        return visitor.visit(this);
    }
}
