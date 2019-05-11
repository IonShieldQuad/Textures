package core;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import core.TextureUtils.*;

public class TextureDisplay extends JPanel {
    private static final Color MODEL_COLOR = new Color(0xff5599);
    
    private Map<Polygon, Transform3D> polygons = new HashMap<>();
    private BufferedImage texture;
    private Filtering filtering = Filtering.OFF;
    
    private double scale = 1.0;
    private boolean showOutline;
    private boolean useMipmap;
    
    //All angles are in radians!
    
    public TextureDisplay() {
        super();
    }
    
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (texture != null) {
            for (Polygon poly : polygons.keySet()) {
                drawPolygon(g, poly.applyTransform(polygons.get(poly)), texture);
            }
        }
        
    }
    
    private void drawPolygon(Graphics g, Polygon p, BufferedImage texture) {
        //System.out.println("Polygon: " + p.a + p.b + p.c);
        Mipmapper mm = new Mipmapper(texture);
        Point3D lineDir = new Point3D(0, 0 ,1);
        Polygon proj = new Polygon(p.a.copy(), p.b.copy(), p.c.copy());
        proj.a.setZ(0);
        proj.b.setZ(0);
        proj.c.setZ(0);
        
        int minX = (int)Math.round(Math.min(Math.min(p.a.getX(), p.b.getX()), p.c.getX()));
        int minY = (int)Math.round(Math.min(Math.min(p.a.getY(), p.b.getY()), p.c.getY()));
        int maxX = (int)Math.round(Math.max(Math.max(p.a.getX(), p.b.getX()), p.c.getX()));
        int maxY = (int)Math.round(Math.max(Math.max(p.a.getY(), p.b.getY()), p.c.getY()));
        //System.out.println("minX: " + minX + ", minY: " + minY + ", maxX: " + maxX + ", maxY: " + maxY);
        
        for (int i = minY; i < maxY; i++) {
            for (int j = minX; j < maxX; j++) {
                Point3D pos = new Point3D(j, i, 0);
                Point3D point = new Point3D(j, i, 0);
                if (proj.contains(point)) {
                    PointDouble uv = proj.uv(point);
                    //System.out.println("UV: " + uv.getX() + ":" +uv.getY());
                    PointDouble uvl = proj.uv(new Point3D(j - 1, i, 0));
                    PointDouble uvr = proj.uv(new Point3D(j + 1, i, 0));
                    PointDouble uvt = proj.uv(new Point3D(j, i - 1, 0));
                    PointDouble uvb = proj.uv(new Point3D(j, i + 1, 0));
                    
                    double dudx = ((Math.abs(uv.getX() - uvl.getX()) + Math.abs(uv.getX() - uvr.getX())) / 2) * mm.getMipmap(0, 0).getWidth();
                    double dvdy = ((Math.abs(uv.getY() - uvt.getY()) + Math.abs(uv.getY() - uvb.getY())) / 2) * mm.getMipmap(0, 0).getHeight();
                    double mmU = Math.max(0, Math.log(dudx) / Math.log(2));
                    double mmV = Math.max(0, Math.log(dvdy) / Math.log(2));
                    //System.out.println("du/dx: " + dudx + ", dv/dy: " + dvdy + ", mmU: " + mmU + ", mmV: " + mmV);
                    
                    Color c = mm.getColor(uv.getX(), 1 - uv.getY(), useMipmap ? mmU : 0, useMipmap ? mmV : 0, filtering);
                    g.setColor(c);
                    g.drawRect(normX(j), normY(i), 0, 0);
                }
            }
        }
        if (showOutline) {
            g.setColor(MODEL_COLOR);
            g.drawLine(normX(p.a.getX()), normY(p.a.getY()), normX(p.b.getX()), normY(p.b.getY()));
            g.drawLine(normX(p.b.getX()), normY(p.b.getY()), normX(p.c.getX()), normY(p.c.getY()));
            g.drawLine(normX(p.c.getX()), normY(p.c.getY()), normX(p.a.getX()), normY(p.a.getY()));
        }
    }
    
    private double interpolate(double a, double b, double alpha) {
        return b * alpha + a * (1 - alpha);
    }
    
    private Color interpolate(Color c1, Color c2, double alpha) {
        double gamma = 2.2;
        int r = (int)Math.round(255 * Math.pow(Math.pow(c2.getRed() / 255.0, gamma) * alpha + Math.pow(c1.getRed() / 255.0, gamma) * (1 - alpha), 1 / gamma));
        int g = (int)Math.round(255 * Math.pow(Math.pow(c2.getGreen() / 255.0, gamma) * alpha + Math.pow(c1.getGreen() / 255.0, gamma) * (1 - alpha), 1 / gamma));
        int b = (int)Math.round(255 * Math.pow(Math.pow(c2.getBlue() / 255.0, gamma) * alpha + Math.pow(c1.getBlue() / 255.0, gamma) * (1 - alpha), 1 / gamma));
        
        return new Color(r, g, b);
    }
    
    public int normX(double x) {
        return (int)Math.round((x / getScale()) + 0.5 * getWidth());
    }
    public int normY(double y) {
        return (int)Math.round(0.5 * getHeight() - (y / getScale()));
    }
    
    public double getScale() {
        return scale;
    }
    
    public void setScale(double scale) {
        this.scale = scale;
    }
    
    public Map<Polygon, Transform3D> getPolygons() {
        return polygons;
    }
    
    public BufferedImage getTexture() {
        return texture;
    }
    
    public void setTexture(BufferedImage texture) {
        this.texture = texture;
    }
    
    public Filtering getFiltering() {
        return filtering;
    }
    
    public void setFiltering(Filtering filtering) {
        this.filtering = filtering;
    }
    
    public boolean isShowOutline() {
        return showOutline;
    }
    
    public void setShowOutline(boolean showOutline) {
        this.showOutline = showOutline;
    }
    
    public boolean isUseMipmap() {
        return useMipmap;
    }
    
    public void setUseMipmap(boolean useMipmap) {
        this.useMipmap = useMipmap;
    }
}

