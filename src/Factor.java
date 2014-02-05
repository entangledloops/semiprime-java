import java.math.BigInteger;
import java.util.ArrayList;

/**
 * @author Stephen Dunn
 */

public class Factor {
  Node root, current;
  ArrayList<Node> open = new ArrayList<Node>();
  ArrayList<Node> closed = new ArrayList<Node>();
  BigInteger sp;
  String strSP;
  public int depth, generated, expanded;

  public Factor(BigInteger sp)
  {
    this.sp = sp;
    strSP = sp.toString(10);
    root = new Node(); current = root;

    char one = strSP.charAt(strSP.length()-1);
    Node p = null, q = null;

    switch(one)
    {
      case '1':
        p = new Node(root,1,1);
        q = new Node(root,9,9);
        break;
      case '3':
        p = new Node(root,1,3);
        q = new Node(root,7,9);
        break;
      case '7':
        p = new Node(root,1,7);
        q = new Node(root,3,9);
        break;
      case '9':
        p = new Node(root,1,9);
        q = new Node(root,7,7);
        break;
      default:
        System.out.println("Bad input: " + strSP);
        System.exit(0);
    }

    generated += 2;
    open.add(p); open.add(q);
  }

  public int pow(int b, int e)
  {
    if (e == 0) return 1;
    if (e == 1) return b;
    return b*pow(b,e-1);
  }

  public int expand()
  {
    //****************** safety checks *****************
    // check if at max digit in sp:
    if (current==null) { // any nodes left?
      if (open.size() == 0) return 0;
      current = open.get(open.size() - 1);
      if (current==null) { out("null node in open"); return 0; } // double-check
    }

    // uncomment to prevent duplicate expansions, but risk manually skipping nodes in open -->
    //open.remove(current);
    boolean redo=false;
    for (Node n : closed)
      if (current==n) { redo=true; break; }
    if (redo) return 0;

    closed.add(current);

    // goal test -->
    if (goalTest()) return 0;

    // check for disqualifying node features -->
    if (current.depth+1 > strSP.length()) return 0;
    if (current.sum.compareTo(sp) > 0) {
       return 0;
    }

    //********* update counters / local var init. ******
    int children = 0;
    expanded++;
    depth = current.depth; // update the current depth record

    //************generate children ********************

    // this value is used for "shifting" integers into the proper
    // value place for addition into the running sum
    String strShift = "1";
    for (int i=0; i<depth; i++) strShift += "0";
    BigInteger shift = new BigInteger(strShift);

    // get the current digit out of sp that we're looking for --->
    char cur = strSP.charAt(strSP.length()-depth-1);

    // now identify all combinations that work --->
    BigInteger temp;

    for (int i=0; i<10; i++)
    {
      for (int j=0; j<10; j++)
      {

      }
      //if ((""+i).charAt(0)==cur) {}

    }

    //******* calculate new sum for children ***********



    return children;
  }

  public void up()
  {
    if (current==null||current.parent==null) { out("already at minimum depth"); return; }
    current = current.parent; depth = current.depth;
  }

  public boolean goalTest()
  {
    return current != null && current.sum.compareTo(sp) == 0;
  }

  public void search()
  {

  }

  public Node findNode(int r, int c)
  {
    for (Node n : open)
      if (n.p==r && n.q==c) return n;
    return null;
  }

  public String toString() { return strSP; }

  class Node
  {
    Node parent;
    BigInteger sum;
    int p, q, depth;

    public Node()
    {
      sum = new BigInteger("0");
    }

    public Node(Node parent, int p, int q)
    {
      this.p = p; this.q = q; this.parent = parent;
      depth = parent.depth +1;
      sum = parent.sum.add(new BigInteger(""+p*q));
    }

    public String toString()
    {
      return sum.toString(10);
    }
      
    public String getP()
    {
      return (parent==null) ? "" : p+parent.getP();
    }

    public String getQ()
    {
      return (parent==null) ? "" : q+parent.getQ();
    }
  }

  public static void out(String s) { System.out.println(s); }
}
