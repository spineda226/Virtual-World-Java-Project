import processing.core.PImage;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class AbstractMoveableEntity extends AbstractAnimatedEntity implements Movable
{
    public AbstractMoveableEntity(Point position, List<PImage> images, String id, int actionPeriod, int animationPeriod)
    {
        super(position, images, id, actionPeriod, animationPeriod);
    }

    private PathingStrategy strategy = new AStarPathingStrategy();

    protected abstract EntityVisitor<Boolean> getTargetEntityVisitor();

    protected abstract void _applyMoveableActivity(Optional<Entity> target, WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    protected abstract void _applyAdjacent(WorldModel world, Entity target, EventScheduler scheduler);

    protected abstract boolean _applyCanPassThrough(WorldModel world, Point p);

    protected int getRepeatCount()
    {
        return 0;
    }

    protected void eliminateEntity(WorldModel world, Entity entity, EventScheduler scheduler)
    {
        world.removeEntity(entity);
        scheduler.unscheduleAllEvents(entity);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> target = world.findNearest(getPosition(), getTargetEntityVisitor());
        _applyMoveableActivity(target, world, imageStore, scheduler);
    }

    private static boolean neighbors(Point p1, Point p2)
    {
        return p1.getX() +1 == p2.getX() && p1.getY() == p2.getY() ||
                p1.getX()-1 == p2.getX() && p1.getY() == p2.getY() ||
                p1.getX() == p2.getX() && p1.getY()+1 == p2.getY() ||
                p1.getX() == p2.getX() && p1.getY()-1 == p2.getY();
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler)
    {
        if (Point.adjacent(getPosition(), target.getPosition()))
        {
            _applyAdjacent(world, target, scheduler);
            return true;
        }
        else
        {
            Point nextPos = nextPosition(world, target.getPosition());

            if (nextPos == null)
                return false;

            if (!getPosition().equals(nextPos))
            {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent())
                {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

    public Point nextPosition(WorldModel world, Point destPoint)
    {
        List<Point> points = strategy.computePath(getPosition(), destPoint,
                p -> (_applyCanPassThrough(world, p)),
                (p1, p2) -> neighbors(p1, p2),
                PathingStrategy.CARDINAL_NEIGHBORS);

        if (points.size() == 0)
            return null;
        else
            return points.get(0);
    }
}
