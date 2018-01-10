public class AllFalseEntityVisitor implements EntityVisitor<Boolean>
{
    public Boolean visit(MinerNotFull minerNotFull) { return false; }
    public Boolean visit(MinerFull minerFull) { return false; }
    public Boolean visit(OreBlob oreBlob) { return false; }
    public Boolean visit(Quake quake) { return false; }
    public Boolean visit (Vein vein) { return false; }
    public Boolean visit(Ore ore) { return false; }
    public Boolean visit(Obstacle obstacle) { return false; }
    public Boolean visit(Blacksmith blacksmith) { return false; }
    public Boolean visit(Bee bee) { return false; }
    public Boolean visit(Grave grave) { return false; }
    public Boolean visit(Butterfly butterfly) { return false; };
}
