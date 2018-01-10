import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Butterfly extends AbstractMoveableEntity
{
    private static final Random rand = new Random();
    private static final int VEIN_ACTION_MIN = 10000;
    private static final int VEIN_ACTION_MAX = 20000;

    public Butterfly(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(position, images, id, actionPeriod, animationPeriod);
    }

    protected EntityVisitor<Boolean> getTargetEntityVisitor() { return new GraveVisitor(); }

    protected void _applyMoveableActivity(Optional<Entity> target, WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        if (target.isPresent())
        {
            Point pos = target.get().getPosition();
            if (moveTo(world, target.get(), scheduler))
            {
                eliminateEntity(world, this, scheduler);
                Vein vein = new Vein(VirtualWorld.VEIN_KEY, pos, imageStore.getImageList(VirtualWorld.VEIN_KEY),
                        VEIN_ACTION_MIN + rand.nextInt(VEIN_ACTION_MAX-VEIN_ACTION_MIN));
                world.addEntity(vein);
                vein.scheduleActions(scheduler, world, imageStore);
            }
            else
                scheduleActivity(scheduler, world, imageStore, getActionPeriod());
        }
        else
        {
            scheduleActivity(scheduler, world, imageStore, getActionPeriod());
        }
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
