import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigInteger;

public class GUI extends JFrame {
  public static final int WIDTH = 1024, HEIGHT = 768;
  public static Factor factor;
  GUIPanel panel;

  public GUI(BigInteger sp)
  {
    super("Semiprime Heuristic Search");

    factor = new Factor(sp);

    MouseListener listener = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
      }
    };

    panel = new GUIPanel();
    panel.addMouseListener(listener);
    panel.setLayout(null);

    setContentPane(panel);
    setSize(WIDTH,HEIGHT);
    setLocationRelativeTo(null);
    setResizable(false);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setVisible(true);

    updateGUI();
  }

  public void updateGUI()
  {
    for (int i=0;i<10;i++)
      for (int j=0;j<10;j++)
        panel.node[i][j].setVisible(false);

    out(factor.open.size() + " nodes on open");
    for (Factor.Node n : factor.open)
    {
      // skip rendering nodes with the wrong parent
      if (factor.current != n.parent) continue;

      JButton btn = panel.node[n.p][n.q];
      btn.setText(n.q + ", " + n.p);
      btn.setVisible(true);
    }

    panel.stats.setText("<html>Expanded: " + factor.expanded + "<br>" +
        "Generated: " + factor.generated + "<br>" +
        "Current Depth: " + factor.depth + "<br>" +
        "Current Sum: " + factor.current.sum.toString(10) + "<br>" +
        "</html>");

    String sum = factor.current.sum.toString(10);
    panel.pq.setText("<html><h3>P = ..." + factor.current.getP() + "<br>" +
        "Q = ..." + factor.current.getQ() + "<br><hr>" +
        "&nbsp;&nbsp;..." + (sum.equals("0") ? "" : sum) +
        "</h3></html>");

    repaint();
  }

  class GUIPanel extends JPanel {
    JButton node[][] = new JButton[10][10];
    JLabel stats = new JLabel("");
    JLabel pq = new JLabel("");

    public GUIPanel()
    {
      stats.setSize(100, 60);
      stats.setLocation(10, 10);
      stats.setFocusable(false);
      stats.setVisible(true);
      this.add(stats);

      pq.setSize(150, 70);
      pq.setLocation(GUI.WIDTH / 2 - 50, 30);
      pq.setFocusable(false);
      pq.setVisible(true);
      this.add(pq);

      JButton search = new JButton("Run Heuristic Search");
      search.setLocation(GUI.WIDTH / 2 - 120, GUI.HEIGHT - 80);
      search.setSize(190, 30);
      search.setFocusable(false);
      search.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          out("Automated search commencing...");
          factor.search();
        }
      });
      this.add(search);

      JButton up = new JButton("Up \u2191");
      up.setLocation(GUI.WIDTH / 2 + 250, 75);
      up.setSize(80, 30);
      up.setFocusable(false);
      up.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          factor.up();
          updateGUI();
        }
      });
      this.add(up);

      // generate node selection buttons --->
      for (int i=0; i<10; i++)
      {
        for (int j=0; j<10; j++)
        {
          node[i][j] = new NodeButton(i,j);
          this.add(node[i][j]);
        }
      }

    }

    public void paintComponent(Graphics g)
    {
      g.drawRect(0, 120, GUI.WIDTH-11, GUI.HEIGHT-260);
      //g.drawString("Factoring:\n" + factor.toString(), GUI.WIDTH/2-100, 30);
    }
  }

  class NodeButton extends JButton
  {
    int nodeWidth = 100, nodeHeight = 50, xOffset = 7, yOffset = 125;
    int row, col;

    public NodeButton(int i, int j)
    {
      super("\u2205");
      row = i; col = j;
      setSize(nodeWidth, nodeHeight);
      setLocation(xOffset + (nodeWidth * i), yOffset + (nodeHeight * j));
      setFocusable(false);
      setVisible(false);

      addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e)
        {
          setVisible(false);
          out("Expanding (" + getText() + ")...");
          factor.current = factor.findNode(row, col);
          //out("selected: " + factor.current.p + ", " + factor.current.q);

          int ret = factor.expand();
          if (factor.goalTest())
          {
            JOptionPane.showMessageDialog(null,"Factors found:\n" +
                "p = " + factor.current.getP() + "\n" +
                "q = " + factor.current.getQ(),
                "Success",JOptionPane.INFORMATION_MESSAGE);
          }
          else if (ret==0) out("No nodes left to expand");
          else out("Expanded to depth " + factor.depth + " and generated " + ret + " new children");

          updateGUI();
        }
      });
    }

    public void setRowCol(int r, int c) { row = r; col = c; }
    public int getRow() { return row; }
    public int getCol() { return col; }
  }

  public static void out(String s) { System.out.println(s); }

  public static void main(String [] args)
  {
    JFrame.setDefaultLookAndFeelDecorated(true);

    BigInteger sp = null;
    while (true)
    {
      try
      {
        String ret = JOptionPane.showInputDialog("Enter a semiprime number to factor:");
        if (ret == null) System.exit(0);

        sp = new BigInteger(ret);
        String str = sp.toString(10);
        if (str.length() < 2) {
          JOptionPane.showMessageDialog(null, "Please enter a number > 9.", "Weak Input", JOptionPane.INFORMATION_MESSAGE);
          continue;
        }
        char c = str.charAt(str.length()-1);
        if (c == '5' || c == '0') {
          JOptionPane.showMessageDialog(null, "That number is trivially divisible by 5. Please enter a stronger number.", "Weak Input", JOptionPane.INFORMATION_MESSAGE);
          continue;
        }
        if (c != '1' && c != '3' && c != '7' && c != '9') throw new Exception();
        break;

      } catch (Exception e)
      {
        JOptionPane.showMessageDialog(null, "Your input wasn't valid or it wasn't semiprime.", "Bad Input", JOptionPane.INFORMATION_MESSAGE);
      }
    }

    new GUI(sp);
  }

}
