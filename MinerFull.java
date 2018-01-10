import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class MinerFull extends AbstractMiner
{
    public MinerFull(String id, Point position, List<PImage> images, int resourceLimit, int actionPeriod, int animationPeriod)
    {
        super(position, images, id, actionPeriod, animationPeriod, resourceLimit, resourceLimit);
    }

    public void _applyMoveableActivity(Optional<Entity> target, WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        if (target.isPresent() &&
                moveTo(world, target.get(), scheduler))
        {
            transformFull(world, scheduler, imageStore);
        }
        else
        {
            scheduleActivity(scheduler, world, imageStore, getActionPeriod());
        }
    }

    protected EntityVisitor<Boolean> getTargetEntityVisitor() { return new BlacksmithVisitor(); };

    private void transformFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        MinerNotFull miner = new MinerNotFull(getId(), getPosition(), getImages(),
                getResourceLimit(), getActionPeriod(), getAnimationPeriod());

        replaceEntity(world, scheduler, imageStore, miner);
    }

    public <R> R accept(EntityVisitor<R> visitor)
    {
        return visitor.visit(this);
    }
}
