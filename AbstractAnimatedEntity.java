import processing.core.PImage;
import java.util.List;

public abstract class AbstractAnimatedEntity extends AbstractActiveEntity implements Animated
{
    private int animationPeriod;

    public AbstractAnimatedEntity(Point position, List<PImage> images, String id, int actionPeriod, int animationPeriod)
    {
        super(position, images, id, actionPeriod);
        this.animationPeriod = animationPeriod;
    }

    protected abstract int getRepeatCount();

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
    {
        scheduleActivity(scheduler, world, imageStore, getActionPeriod());
        scheduler.scheduleEvent(this,
                new Animation(this, getRepeatCount()),
                getAnimationPeriod());
    }

    public int getAnimationPeriod()
    {
        return animationPeriod;
    }

    public void nextImage()
    {
        setImageIndex((getImageIndex() + 1) % getImages().size());
    }
}

