public class Activity implements Action
{
    private Entity entity;
    private WorldModel world;
    private ImageStore imageStore;

    public Activity(Entity entity, WorldModel world, ImageStore imageStore)
    {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }

    public WorldModel getWorld()
    {
        return world;
    }

    public ImageStore getImageStore()
    {
        return imageStore;
    }

    public void executeAction(EventScheduler scheduler)
    {
        ((Active)entity).executeActivity(getWorld(), getImageStore(), scheduler);
    }
}
