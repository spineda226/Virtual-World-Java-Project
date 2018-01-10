import processing.core.PImage;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class OreBlob extends AbstractMoveableEntity
{
    private static final String QUAKE_KEY = "quake";
    private static final String BUTTERFLY_KEY = "butterfly";
    private static final Random rand = new Random();
    private static final int BUTTERFLY_ACTION_MIN = 2000;
    private static final int BUTTERFLY_ACTION_MAX = 4000;
    private static final int BUTTERFLY_ANIMATION_MIN = 50;
    private static final int BUTTERFLY_ANIMATION_MAX = 150;

    public OreBlob(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(position, images, id, actionPeriod, animationPeriod);
    }

    protected EntityVisitor<Boolean> getTargetEntityVisitor() { return new VeinVisitor(); }

    private void worldEventEffect(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Point pos = getPosition();  // store current position before removing

        eliminateEntity(world, this, scheduler);

        Butterfly butterfly = new Butterfly(BUTTERFLY_KEY, pos, imageStore.getImageList(BUTTERFLY_KEY),
                rand.nextInt(BUTTERFLY_ACTION_MAX - BUTTERFLY_ACTION_MIN) + BUTTERFLY_ACTION_MIN,
                rand.nextInt(BUTTERFLY_ANIMATION_MAX - BUTTERFLY_ANIMATION_MIN) + BUTTERFLY_ANIMATION_MIN);

        world.addEntity(butterfly);
        butterfly.scheduleActions(scheduler, world, imageStore);
    }

    protected void _applyMoveableActivity(Optional<Entity> target, WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Point[] adjacentNeighbors;
        adjacentNeighbors = PathingStrategy.CARDINAL_NEIGHBORS.apply(getPosition()).toArray(Point[]::new);
        boolean affected = withinReachWorldEvent(adjacentNeighbors, world);

        if (affected == true)
        {
            worldEventEffect(world, imageStore, scheduler);
        }
        else
        {
            long nextPeriod = getActionPeriod();
            if (target.isPresent())
            {
                Point tgtPos = target.get().getPosition();

                if (moveTo(world, target.get(), scheduler))
                {
                    Quake quake = new Quake(tgtPos,
                            imageStore.getImageList(QUAKE_KEY));

                    world.addEntity(quake);
                    nextPeriod += getActionPeriod();
                    quake.scheduleActions(scheduler, world, imageStore);
                }
            }
            scheduleActivity(scheduler, world, imageStore, nextPeriod);
        }
    }

    private boolean withinReachWorldEvent(Point[] adjacentNeighbors, WorldModel world)
    {
        for (int i = 0; i < adjacentNeighbors.length; i++)
        {
            Point pos = adjacentNeighbors[i];
            Background[][] background = world.getBackground();
            if (world.withinBounds(pos))
            {
                String backGroundID = background[pos.getY()][pos.getX()].getId();
                if (backGroundID.equals("flower"))
                {
                    return true;
                }
            }
        }
        return false;
    }

    protected void _applyAdjacent(WorldModel world, Entity target, EventScheduler scheduler)
    {
        eliminateEntity(world, target, scheduler);
    }

    protected boolean _applyCanPassThrough(WorldModel world, Point p)
    {
        return world.withinBounds(p) && (!world.isOccupied(p) || world.getOccupancyCell(p).accept(new OreVisitor()));
    }

    public <R> R accept(EntityVisitor<R> visitor)
    {
        return visitor.visit(this);
    }
}
