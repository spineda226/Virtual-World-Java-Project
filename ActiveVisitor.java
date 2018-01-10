public class ActiveVisitor extends AllFalseEntityVisitor
{
    public Boolean visit(MinerNotFull minerNotFull) { return true; }
    public Boolean visit(MinerFull minerFull) { return true; }
    public Boolean visit(OreBlob oreBlob) { return true; }
    public Boolean visit(Quake quake) { return true; }
    public Boolean visit (Vein vein) { return true; }
    public Boolean visit(Ore ore) { return true; }
    public Boolean visit(Bee bee) { return true; }
    public Boolean visit(Butterfly butterfly) { return true; }
}
