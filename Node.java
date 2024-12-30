import java.util.*;

class Node {           // initialize nodes
    char character;
    int freq;
    Node left;
    Node right;

    Node(char character, int freq) {       
        this.character = character;
        this.freq = freq;
        this.left = null;
        this.right = null;
    }
}


class NodeComparator implements Comparator<Node> {      // comparison logic
    @Override
    public int compare(Node n1, Node n2) {     // arrange nodes by frequency
        return n1.freq - n2.freq;
    }
}