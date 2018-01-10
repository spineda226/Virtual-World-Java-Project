import java.util.*;
import java.util.function.Predicate;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.Collectors;

class AStarPathingStrategy implements PathingStrategy
{
    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        LinkedList<Node> closedList = new LinkedList<>();
        LinkedList<Point> path = new LinkedList<>();

        PriorityQueue<Node> openList = new PriorityQueue<>(100, (Node n1, Node n2) -> (n1.fDist - n2.fDist));
        HashMap<Point, Node> openListHash = new HashMap<>();

        Node currentNode = new Node(start, null, 0, calculateHeuristicDist(start, end));

        openList.add(currentNode);
        openListHash.put(currentNode.position, currentNode);

        while (openList.size() != 0)
        {
            currentNode = openList.peek();

            if (withinReach.test(currentNode.position, end))
            {
                while (currentNode.priorSquare != null)
                {
                    path.add(0, currentNode.position);
                    currentNode = currentNode.priorSquare;
                }
                return path;
            }

            List<Point> points = potentialNeighbors.apply(currentNode.position)
                    .filter(canPassThrough)
                    .filter(pt -> !pt.equals(start) && !pt.equals(end))
                    .collect(Collectors.toList());

            for (Point p : points)
            {
                Node neighbor = new Node(p, currentNode, currentNode.gDist + 1, calculateHeuristicDist(p, end));
                if (!closedList.contains(neighbor))
                {
                    if (openListHash.containsKey(p))
                    {
                        Node node = openListHash.get(p);
                        if (node.gDist > currentNode.gDist +1)
                        {
                            openList.remove(node);
                            openListHash.remove(p);

                            node.gDist = currentNode.gDist + 1;
                            node.priorSquare = currentNode;

                            openList.add(node);
                            openListHash.put(node.position, node);
                        }
                    }
                    else
                    {
                        openList.add(neighbor);
                        openListHash.put(neighbor.position, neighbor);
                    }
                }
            }
            openList.remove(currentNode);
            openListHash.remove(currentNode.position);

            closedList.add(currentNode);
        }
        return path;
    }

    private int calculateHeuristicDist(Point p1, Point p2)
    {
        return Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY());
    }

    private class Node
    {
        private Point position;
        private Node priorSquare;
        private int gDist, hDist, fDist;

        public Node(Point position, Node priorSquare, int gDist, int hDist)
        {
            this.position = position;
            this.priorSquare = priorSquare;
            this.gDist = gDist;
            this.hDist = hDist;
            this.fDist = gDist + hDist;
        }

        public boolean equals(Object other)
        {
            if (other == null)
                return false;
            if (getClass() != other.getClass())
                return false;
            Node n = (Node)other;
            return position.equals(n.position); // say equal if positions are equal so you can check if in open or closed list easily
        }
    }

}
