public class Animation implements Action
{
    private Entity entity;
    private int repeatCount;

    public Animation(Entity entity, int repeatCount)
    {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler)
    {
        ((Animated)entity).nextImage();

        if (repeatCount != 1)
        {
            scheduler.scheduleEvent(entity,
                    new Animation(entity, Math.max(repeatCount - 1, 0)),
                    ((Animated)entity).getAnimationPeriod());
        }
    }
}
