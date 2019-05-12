package core;


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import core.TextureUtils.Filtering;

public class MainWindow {
    private static final String TITLE = "Textures";
    private JPanel rootPanel;
    private JTextArea log;
    private JButton displayButton;
    private TextureDisplay graph;
    private JTextField offXField;
    private JTextField offYField;
    private JTextField rotZField;
    private JTextField scaleXField;
    private JTextField scaleYField;
    private JList<String> list1;
    private JTextField VField;
    private JTextField UField;
    private JTextField YField;
    private JTextField XField;
    private JButton addButton;
    private JButton removeButton;
    private JButton clearButton;
    private JButton selectTextureButton;
    private JTextField textField5;
    private JTextField rotXField;
    private JTextField rotYField;
    private JComboBox filteringBox;
    private JTextField offZField;
    private JCheckBox showOutline;
    private JCheckBox useMipmapsCheckBox;
    private JButton addSquare;
    
    //private List<Model> models = new ArrayList<>();
    private List<Point3D> points = new ArrayList<>();
    private BufferedImage texture;
    
    private MainWindow() {
        initComponents();
        
        list1.setModel(new DefaultListModel<>());
        
    }
    
    private void initComponents() {
        displayButton.addActionListener(e -> display());
        addButton.addActionListener(e -> addPoint());
        removeButton.addActionListener(e -> removePoint());
        clearButton.addActionListener(e -> clearPoints());
        selectTextureButton.addActionListener(e -> selectTexture());
        addSquare.addActionListener(e -> addSquare());
    }
    
    private void display() {
        if (points.size() < 3) {
            log.append("\r\nNumber of points should be at least 3");
        }
        try {
            graph.getPolygons().clear();
            
            double offX = Double.parseDouble(offXField.getText());
            double offY = Double.parseDouble(offYField.getText());
            double offZ = Double.parseDouble(offZField.getText());
            
            double rotX = Double.parseDouble(rotXField.getText());
            double rotY = Double.parseDouble(rotYField.getText());
            double rotZ = Double.parseDouble(rotZField.getText());
            
            double scaleX = Double.parseDouble(scaleXField.getText());
            double scaleY = Double.parseDouble(scaleYField.getText());
            double scaleZ = 1;
            
            Transform3D transform = new Transform3D(new Point3D(offX, offY, offZ), new Point3D(Math.toRadians(rotX), Math.toRadians(rotY), Math.toRadians(rotZ)), new Point3D(scaleX, scaleY, scaleZ));
            
            graph.getPolygons().clear();
            graph.setTexture(texture);
            graph.setShowOutline(showOutline.isSelected());
            graph.setUseMipmap(useMipmapsCheckBox.isSelected());
            
            List<Polygon> polygons = new ArrayList<>();
            for (int i = 0; i < points.size() - 2; i++) {
                polygons.add(new Polygon(points.get(0), points.get(i + 1), points.get(i + 2)));
            }
    
            Map<Polygon, Transform3D> map = graph.getPolygons();
            polygons.forEach(p -> map.put(p, transform));
            
            switch (filteringBox.getSelectedIndex()) {
                case 0:
                    graph.setFiltering(Filtering.OFF);
                    break;
                case 1:
                    graph.setFiltering(Filtering.BILINEAR);
                    break;
                case 2:
                    graph.setFiltering(Filtering.TRILINEAR);
                    break;
                case 3:
                    graph.setFiltering(Filtering.ANISOTROPIC);
                    break;
            }
            
            updateGraph();
        }
        catch (NumberFormatException e) {
            log.append("Invalid format!\n");
        }
    }
    
    private void selectTexture() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "JPG & GIF & PNG Images", "jpg", "gif", "png");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(rootPanel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage img = ImageIO.read(chooser.getSelectedFile());
                textField5.setText(chooser.getSelectedFile().getName());
                texture = img;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void addPoint() {
        try {
            points.add(new Point3D(Double.parseDouble(XField.getText()), Double.parseDouble(YField.getText()), 0, Double.parseDouble(UField.getText()), Double.parseDouble(VField.getText())));
        }
        catch (NumberFormatException e) {
            log.append("\r\nInvalid number format");
        }
        updateList();
    }
    
    private void removePoint() {
        if (list1.getSelectedIndex() != -1) {
            points.remove(list1.getSelectedIndex());
            updateList();
        }
    }
    
    private void addSquare() {
        points.add(new Point3D(-100, -100, 0, 0, 0));
        points.add(new Point3D(100, -100, 0, 1, 0));
        points.add(new Point3D(100, 100, 0, 1, 1));
        points.add(new Point3D(-100, 100, 0, 0, 1));
        updateList();
    }
    
    private void clearPoints() {
        points.clear();
        updateList();
    }
    
    private void updateGraph() {
        graph.repaint();
    }
    
    private void updateList() {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (Point3D point: points) {
            model.addElement("X: " + point.getX() + " Y: " + point.getY() + " U: " + point.getU() + " V: " + point.getV());
        }
        list1.setModel(model);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame(TITLE);
        MainWindow gui = new MainWindow();
        frame.setContentPane(gui.rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    
}
