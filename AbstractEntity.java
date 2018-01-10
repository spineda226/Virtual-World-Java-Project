import processing.core.PImage;
import java.util.List;

public abstract class AbstractEntity implements Entity
{
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;

    public AbstractEntity(Point position, List<PImage> images, String id)
    {
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.id = id;
    }
    protected String getId() { return id; }

    protected List<PImage> getImages() { return images; }

    protected void setImageIndex(int imageIndex) { this.imageIndex = imageIndex; }

    protected int getImageIndex() { return imageIndex; }

    public Point getPosition()
    {
        return position;
    }

    public void setPosition(Point position)
    {
        this.position = position;
    }

    public PImage getCurrentImage()
    {
        return images.get(imageIndex);
    }
}
