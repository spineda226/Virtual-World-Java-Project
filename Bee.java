import processing.core.PImage;
import java.util.List;
import java.util.Optional;

public class Bee extends AbstractMoveableEntity
{
    private static final String GRAVE_KEY = "grave";

    public Bee(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(position, images, id, actionPeriod, animationPeriod);
    }

    protected EntityVisitor<Boolean> getTargetEntityVisitor() { return new BeeAttackVisitor(); }

    protected void _applyMoveableActivity(Optional<Entity> target, WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        long nextPeriod = getActionPeriod();

        if (target.isPresent())
        {
            Point tgtPos = target.get().getPosition();

            if (moveTo(world, target.get(), scheduler))
            {
                eliminateEntity(world, this, scheduler);
                Grave grave = new Grave(GRAVE_KEY, tgtPos, imageStore.getImageList(GRAVE_KEY));
                world.addEntity(grave);
                nextPeriod += getActionPeriod();
            }
        }
        scheduleActivity(scheduler, world, imageStore, nextPeriod);
    }

    protected void _applyAdjacent(WorldModel world, Entity target, EventScheduler scheduler)
    {
        eliminateEntity(world, target, scheduler);
    }

    protected boolean _applyCanPassThrough(WorldModel world, Point p)
    {
        return world.withinBounds(p) && !world.isOccupied(p);
    }

    public <R> R accept(EntityVisitor<R> visitor)
    {
        return visitor.visit(this);
    }
}
