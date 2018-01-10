import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import processing.core.*;
import java.util.ArrayList;

public final class VirtualWorld
   extends PApplet
{
   private static final int TIMER_ACTION_PERIOD = 100;

   private static final int VIEW_WIDTH = 640;
   private static final int VIEW_HEIGHT = 480;
   private static final int TILE_WIDTH = 32;
   private static final int TILE_HEIGHT = 32;
   private static final int WORLD_WIDTH_SCALE = 2;
   private static final int WORLD_HEIGHT_SCALE = 2;

   private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
   private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
   private static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
   private static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

   private static final String IMAGE_LIST_FILE_NAME = "imagelist";
   private static final String DEFAULT_IMAGE_NAME = "background_default";
   private static final int DEFAULT_IMAGE_COLOR = 0x808080;

   private static final String LOAD_FILE_NAME = "gaia.sav";

   private static final String FAST_FLAG = "-fast";
   private static final String FASTER_FLAG = "-faster";
   private static final String FASTEST_FLAG = "-fastest";
   private static final double FAST_SCALE = 0.5;
   private static final double FASTER_SCALE = 0.25;
   private static final double FASTEST_SCALE = 0.10;

   private static double timeScale = 1.0;

   private static final int PROPERTY_KEY = 0;
   private static final String BGND_KEY = "background";
   private static final String MINER_KEY = "miner";
   private static final String OBSTACLE_KEY = "obstacle";
   private static final String ORE_KEY = "ore";
   private static final String SMITH_KEY = "blacksmith";
   public static final String VEIN_KEY = "vein";
   private static final String FLOWER_KEY = "flower";

   private static final Random rand = new Random();
   private static final String BEE_KEY = "bee";
   private static final int BEE_ACTION_MIN = 1500;
   private static final int BEE_ACTION_MAX = 2500;
   private static final int BEE_ANIMATION_MIN = 50;
   private static final int BEE_ANIMATION_MAX = 150;

   private ImageStore imageStore;
   private WorldModel world;
   private WorldView view;
   private EventScheduler scheduler;

   private long next_time;

   public void settings()
   {
      size(VIEW_WIDTH, VIEW_HEIGHT);
   }

   /*
      Processing entry point for "sketch" setup.
   */
   public void setup()
   {
      this.imageStore = new ImageStore(
         createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
      this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
         createDefaultBackground(imageStore));
      this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world,
         TILE_WIDTH, TILE_HEIGHT);
      this.scheduler = new EventScheduler(timeScale);

      loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
      loadWorld(world, LOAD_FILE_NAME, imageStore);

      scheduleActions(world, scheduler, imageStore);

      next_time = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
   }

   public void draw()
   {
      long time = System.currentTimeMillis();
      if (time >= next_time)
      {
         scheduler.updateOnTime(time);
         next_time = time + TIMER_ACTION_PERIOD;
      }

      view.drawViewport();
   }

   public void keyPressed()
   {
      if (key == CODED)
      {
         int dx = 0;
         int dy = 0;

         switch (keyCode)
         {
            case UP:
               dy = -1;
               break;
            case DOWN:
               dy = 1;
               break;
            case LEFT:
               dx = -1;
               break;
            case RIGHT:
               dx = 1;
               break;
         }
         view.shiftView(dx, dy);
      }
   }

   public void mousePressed()
   {
      Point currentPos = view.getViewport().viewportToWorld(mouseX/TILE_WIDTH, mouseY/TILE_HEIGHT);
      Point[] neighborsAffected;
      neighborsAffected = PathingStrategy.DIAGONAL_CARDINAL_NEIGHBORS.apply(currentPos).toArray(Point[]::new);
      createWorldEventBackground(neighborsAffected);
      spawnBee(currentPos, world, imageStore, scheduler);
   }

   private void createWorldEventBackground(Point[] affectedSquares)
   {
      for (int i=0; i<affectedSquares.length; i++)
      {
         world.setBackground(affectedSquares[i], new Background(FLOWER_KEY, imageStore.getImageList(FLOWER_KEY)));
      }
   }

   private void spawnBee(Point pos, WorldModel world, ImageStore imageStore, EventScheduler scheduler)
   {
      if (!world.isOccupied(pos))
      {
         Bee bee = new Bee(BEE_KEY, pos, imageStore.getImageList(BEE_KEY),
                 rand.nextInt(BEE_ACTION_MAX - BEE_ACTION_MIN) + BEE_ACTION_MIN,
                 rand.nextInt(BEE_ANIMATION_MAX - BEE_ANIMATION_MIN) + BEE_ANIMATION_MIN);
         world.addEntity(bee);
         bee.scheduleActions(scheduler, world, imageStore);
      }

   }

   private static Background createDefaultBackground(ImageStore imageStore)
   {
      return new Background(DEFAULT_IMAGE_NAME,
         imageStore.getImageList(DEFAULT_IMAGE_NAME));
   }

   private static PImage createImageColored(int width, int height, int color)
   {
      PImage img = new PImage(width, height, RGB);
      img.loadPixels();
      for (int i = 0; i < img.pixels.length; i++)
      {
         img.pixels[i] = color;
      }
      img.updatePixels();
      return img;
   }

   private static void loadImages(String filename, ImageStore imageStore,
      PApplet screen)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         imageStore.loadImages(in, screen);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   private static void loadWorld(WorldModel world, String filename,
      ImageStore imageStore)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         load(in, world, imageStore);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   private static void load(Scanner in, WorldModel world, ImageStore imageStore)
   {
      int lineNumber = 0;
      while (in.hasNextLine())
      {
         try
         {
            if (!processLine(in.nextLine(), world, imageStore))
            {
               System.err.println(String.format("invalid entry on line %d",
                       lineNumber));
            }
         }
         catch (NumberFormatException e)
         {
            System.err.println(String.format("invalid entry on line %d",
                    lineNumber));
         }
         catch (IllegalArgumentException e)
         {
            System.err.println(String.format("issue on line %d: %s",
                    lineNumber, e.getMessage()));
         }
         lineNumber++;
      }
   }

   private static boolean processLine(String line, WorldModel world,
                                      ImageStore imageStore)
   {
      String[] properties = line.split("\\s");
      if (properties.length > 0)
      {
         switch (properties[PROPERTY_KEY])
         {
            case BGND_KEY:
               return Parse.parseBackground(properties, world, imageStore);
            case MINER_KEY:
               return Parse.parseMiner(properties, world, imageStore);
            case OBSTACLE_KEY:
               return Parse.parseObstacle(properties, world, imageStore);
            case ORE_KEY:
               return Parse.parseOre(properties, world, imageStore);
            case SMITH_KEY:
               return Parse.parseSmith(properties, world, imageStore);
            case VEIN_KEY:
               return Parse.parseVein(properties, world, imageStore);
         }
      }

      return false;
   }

   private static void scheduleActions(WorldModel world,
      EventScheduler scheduler, ImageStore imageStore)
   {
      ActiveVisitor activeVisitor = new ActiveVisitor();
      for (Entity entity : world.getEntities())
      {
         if (entity.accept(activeVisitor))
            ((Active)entity).scheduleActions(scheduler, world, imageStore);
      }
   }

   private static void parseCommandLine(String [] args)
   {
      for (String arg : args)
      {
         switch (arg)
         {
            case FAST_FLAG:
               timeScale = Math.min(FAST_SCALE, timeScale);
               break;
            case FASTER_FLAG:
               timeScale = Math.min(FASTER_SCALE, timeScale);
               break;
            case FASTEST_FLAG:
               timeScale = Math.min(FASTEST_SCALE, timeScale);
               break;
         }
      }
   }

   public static void main(String [] args)
   {
      parseCommandLine(args);
      PApplet.main(VirtualWorld.class);
   }
}
