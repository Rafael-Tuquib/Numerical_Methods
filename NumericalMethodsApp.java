import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class NumericalMethodsApp extends JFrame {

    // ================= ROOT COMPONENTS =================
    JComboBox<String> methodBox;
    JComboBox<String> equationBox;
    JTextField customEquationField;
    JTextField xlField, xuField;
    JTextField toleranceField;
    JTextArea outputArea;
    GraphPanel graphPanel;
    JButton computeButton;
    JTable resultsTable;

    // ================= MATRIX COMPONENTS =================
    JTextArea matrixA;
    JTextArea matrixB;
    JTextArea matrixResult;
    JComboBox<String> matrixOp;
    JButton matrixButton;

    public NumericalMethodsApp() {

        setTitle("Numerical Methods Application");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        // ================= ROOT PANEL =================
        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(null);

        // Equation Selection
        JLabel eqLabel = new JLabel("Equation:");
        eqLabel.setBounds(20, 20, 80, 25);
        rootPanel.add(eqLabel);

        String[] equations = {
                "x^3 - x - 2",
                "sin(x) - 0.5",
                "exp(x) - 3",
                "x^2 - 4",
                "cos(x) - x",
                "log(x) - 1",
                "Custom"
        };

        equationBox = new JComboBox<>(equations);
        equationBox.setBounds(100, 20, 150, 25);
        equationBox.addActionListener(e -> updateCustomEquationField());
        rootPanel.add(equationBox);

        JLabel customLabel = new JLabel("Custom Eq:");
        customLabel.setBounds(270, 20, 80, 25);
        rootPanel.add(customLabel);

        customEquationField = new JTextField("x^2 - 4");
        customEquationField.setBounds(350, 20, 150, 25);
        customEquationField.setEnabled(false);
        rootPanel.add(customEquationField);

        // XL, XU
        JLabel xlLabel = new JLabel("XL:");
        xlLabel.setBounds(520, 20, 40, 25);
        rootPanel.add(xlLabel);

        xlField = new JTextField("1");
        xlField.setBounds(560, 20, 80, 25); 
        rootPanel.add(xlField);

        JLabel xuLabel = new JLabel("XU:");
        xuLabel.setBounds(660, 20, 40, 25);
        rootPanel.add(xuLabel);

        xuField = new JTextField("2");
        xuField.setBounds(700, 20, 80, 25);
        rootPanel.add(xuField);

        // Tolerance
        JLabel tolLabel = new JLabel("Tol:");
        tolLabel.setBounds(790, 20, 40, 25);
        rootPanel.add(tolLabel);

        toleranceField = new JTextField("0.0001");
        toleranceField.setBounds(830, 20, 100, 25);
        rootPanel.add(toleranceField);

        // Method
        JLabel methodLabel = new JLabel("Method:");
        methodLabel.setBounds(950, 20, 70, 25);
        rootPanel.add(methodLabel);

        String[] methods = {
                "Incremental",
                "Bisection",
                "Regula Falsi",
                "Newton Raphson",
                "Secant"
        };

        methodBox = new JComboBox<>(methods);
        methodBox.setBounds(1020, 20, 130, 25);
        rootPanel.add(methodBox);

        computeButton = new JButton("Compute");
        computeButton.setBounds(1020, 55, 130, 25);
        computeButton.addActionListener(e -> computeRoots());
        rootPanel.add(computeButton);

        // Results Table
        resultsTable = new JTable();
        JScrollPane tableScroll = new JScrollPane(resultsTable);
        tableScroll.setBounds(20, 70, 600, 400);
        rootPanel.add(tableScroll);

        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setEditable(false);
        JScrollPane scroll1 = new JScrollPane(outputArea);
        scroll1.setBounds(20, 490, 600, 200);
        rootPanel.add(scroll1);

        graphPanel = new GraphPanel();
        graphPanel.setBounds(650, 70, 480, 620);
        graphPanel.setBackground(Color.WHITE);
        rootPanel.add(graphPanel);

        // ================= MATRIX PANEL =================
        JPanel matrixPanel = new JPanel();
        matrixPanel.setLayout(null);

        JLabel labelA = new JLabel("Matrix A:");
        labelA.setBounds(20, 20, 100, 25);
        matrixPanel.add(labelA);

        matrixA = new JTextArea();
        matrixA.setText("1 2\n3 4");
        JScrollPane scrollA = new JScrollPane(matrixA);
        scrollA.setBounds(20, 50, 250, 150);
        matrixPanel.add(scrollA);

        JLabel labelB = new JLabel("Matrix B:");
        labelB.setBounds(300, 20, 100, 25);
        matrixPanel.add(labelB);

        matrixB = new JTextArea();
        matrixB.setText("5 6\n7 8");
        JScrollPane scrollB = new JScrollPane(matrixB);
        scrollB.setBounds(300, 50, 250, 150);
        matrixPanel.add(scrollB);

        String[] matrixOps = {
                "Addition",
                "Multiplication",
                "Transpose",
                "Determinant",
                "Inverse",
                "Adjoint",
                "Power",
                "Equation"
        };

        matrixOp = new JComboBox<>(matrixOps);
        matrixOp.setBounds(600, 50, 180, 30);
        matrixPanel.add(matrixOp);

        matrixButton = new JButton("Compute");
        matrixButton.setBounds(800, 50, 120, 35);
        matrixButton.addActionListener(e -> computeMatrix());
        matrixPanel.add(matrixButton);

        matrixResult = new JTextArea();
        matrixResult.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scrollR = new JScrollPane(matrixResult);
        scrollR.setBounds(20, 250, 900, 300);
        matrixPanel.add(scrollR);

        tabs.add("Root Finding", rootPanel);
        tabs.add("Matrix Operations", matrixPanel);

        add(tabs);
        setVisible(true);
    }

    private void updateCustomEquationField() {
        customEquationField.setEnabled(equationBox.getSelectedItem().equals("Custom"));
    }

    // ================= FUNCTION =================

    static class GraphPanel extends JPanel {

        double[] xPoints = new double[0];
        double[] yPoints = new double[0];

        public void setPoints(double[] x, double[] y) {
            this.xPoints = x;
            this.yPoints = y;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Background
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, w, h);

            // Graph ranges
            double xMin = -10;
            double xMax = 10;
            double yMin = -1000;
            double yMax = 1000;

            int margin = 70;

            // Make graph square
            int graphSize = Math.min(w - 2 * margin, h - 2 * margin);

            int startX = (w - graphSize) / 2;
            int startY = (h - graphSize) / 2;

            double scaleX = graphSize / (xMax - xMin);
            double scaleY = graphSize / (yMax - yMin);

            // Grid
            g2.setColor(new Color(180, 180, 180));

            for (int i = (int) xMin; i <= xMax; i += 5) {

                int x = (int) (startX + (i - xMin) * scaleX);

                g2.drawLine(x, startY, x, startY + graphSize);
            }

            for (int i = (int) yMin; i <= yMax; i += 200) {

                int y = (int) (startY + graphSize - (i - yMin) * scaleY);

                g2.drawLine(startX, y, startX + graphSize, y);
            }

            // Cartesian Axes
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));

            int xAxisY = (int) (startY + graphSize - (0 - yMin) * scaleY);
            int yAxisX = (int) (startX + (0 - xMin) * scaleX);

            g2.drawLine(startX, xAxisY, startX + graphSize, xAxisY);
            g2.drawLine(yAxisX, startY, yAxisX, startY + graphSize);

            // Arrowheads
            Polygon xArrow = new Polygon();
            xArrow.addPoint(startX + graphSize + 15, xAxisY);
            xArrow.addPoint(startX + graphSize - 10, xAxisY - 8);
            xArrow.addPoint(startX + graphSize - 10, xAxisY + 8);
            g2.fillPolygon(xArrow);

            Polygon yArrow = new Polygon();
            yArrow.addPoint(yAxisX, startY - 15);
            yArrow.addPoint(yAxisX - 8, startY + 10);
            yArrow.addPoint(yAxisX + 8, startY + 10);
            g2.fillPolygon(yArrow);

            // Axis Labels
            g2.setFont(new Font("Arial", Font.PLAIN, 12));

            // X-axis numbers
            for (int i = (int) xMin; i <= xMax; i += 5) {

                int x = (int) (startX + (i - xMin) * scaleX);

                g2.drawString(String.valueOf(i), x - 10, startY + graphSize + 20);
            }

            // Y-axis numbers on side
            for (int i = (int) yMin; i <= yMax; i += 200) {

                int y = (int) (startY + graphSize - (i - yMin) * scaleY);

                g2.drawString(String.valueOf(i), startX - 55, y + 5);
            }

            // Axis names
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.drawString("x", startX + graphSize + 20, xAxisY + 8);
            g2.drawString("y", yAxisX - 10, startY - 20);

            // Draw Function Curve
            g2.setColor(new Color(0, 90, 255));
            g2.setStroke(new BasicStroke(3));

            int prevX = -1;
            int prevY = -1;

            for (double x = xMin; x <= xMax; x += 0.01) {

                double y = f(x);

                if (Double.isNaN(y) || Double.isInfinite(y)
                        || y < yMin || y > yMax) {

                    prevX = -1;
                    prevY = -1;
                    continue;
                }

                int px = (int) (startX + (x - xMin) * scaleX);
                int py = (int) (startY + graphSize - (y - yMin) * scaleY);

                if (prevX != -1 && prevY != -1) {
                    g2.drawLine(prevX, prevY, px, py);
                }

                prevX = px;
                prevY = py;
            }

            // Root Points
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2));

            for (int i = 0; i < xPoints.length; i++) {

                double x = xPoints[i];
                double y = yPoints[i];

                if (y < yMin || y > yMax)
                    continue;

                int px = (int) (startX + (x - xMin) * scaleX);
                int py = (int) (startY + graphSize - (y - yMin) * scaleY);

                g2.drawOval(px - 7, py - 7, 14, 14);
            }
        }

        private double f(double x) {
            return x * x * x - x - 2;
        }
    }

    double f(double x) {
        String eq = (String) equationBox.getSelectedItem();
        
        if (eq.equals("Custom")) {
            eq = customEquationField.getText();
        }

        switch (eq) {
            case "x^3 - x - 2":
                return x * x * x - x - 2;
            case "sin(x) - 0.5":
                return Math.sin(x) - 0.5;
            case "exp(x) - 3":
                return Math.exp(x) - 3;
            case "x^2 - 4":
                return x * x - 4;
            case "cos(x) - x":
                return Math.cos(x) - x;
            case "log(x) - 1":
                return Math.log(x) - 1;
            default:
                return x * x * x - x - 2;
        }
    }

    double df(double x) {
        String eq = (String) equationBox.getSelectedItem();
        
        if (eq.equals("Custom")) {
            eq = customEquationField.getText();
        }

        switch (eq) {
            case "x^3 - x - 2":
                return 3 * x * x - 1;
            case "sin(x) - 0.5":
                return Math.cos(x);
            case "exp(x) - 3":
                return Math.exp(x);
            case "x^2 - 4":
                return 2 * x;
            case "cos(x) - x":
                return -Math.sin(x) - 1;
            case "log(x) - 1":
                return 1.0 / x;
            default:
                return 3 * x * x - 1;
        }
    }

    // ================= ROOT METHODS =================

    void computeRoots() {

        java.util.List<Double> xList = new java.util.ArrayList<>();
        java.util.List<Double> yList = new java.util.ArrayList<>();
        java.util.List<Object[]> tableData = new java.util.ArrayList<>();

        outputArea.setText("");

        double xl = Double.parseDouble(xlField.getText());
        double xu = Double.parseDouble(xuField.getText());
        double tolerance = Double.parseDouble(toleranceField.getText());

        String method = (String) methodBox.getSelectedItem();

        outputArea.append("Method: " + method + "\n\n");

        if (method.equals("Incremental")) {
            outputArea.append("i\tXL\tXR\tXU\tf(XL)\tf(XR)\tProduct\n");

            double step = 0.1;
            double prevXL = xl;

            for (int i = 1; i <= 100; i++) {

                double xr = xl + step;
                double fxl = f(xl);
                double fxr = f(xr);
                double product = fxl * fxr;

                xList.add(xr);
                yList.add(fxr);

                tableData.add(new Object[]{
                 i,
                 formatNumber(xl),   // XL
                 formatNumber(xr),   // XR
                 formatNumber(xr),   // XU
                 formatNumber(fxl),
                 formatNumber(fxr),
                    "",
                 formatNumber(product),
                 product < 0 ? "Root found" : ""
                    });

                 outputArea.append(i + "\t"
                    + String.format("%.4f", xl) + "\t"
                    + String.format("%.4f", xr) + "\t"
                    + String.format("%.4f", xr) + "\t"
                    + String.format("%.4f", fxl) + "\t"
                    + String.format("%.4f", fxr) + "\t"
                    + String.format("%.4f", product) + "\n");

                if (product < 0) {
                    outputArea.append("\nRoot found between " + xl + " and " + xr);
                    break;
                }

                xl = xr;
            }
        }

        else if (method.equals("Bisection")) {
            outputArea.append("i\tXL\tXR\tXU\tf(XL)\tf(XR)\tEa\tProduct\tRemarks\n");

            double prevXR = 0;

            for (int i = 1; i <= 100; i++) {

                double xr = (xl + xu) / 2;
                double fxl = f(xl);
                double fxr = f(xr);
                double product = fxl * fxr;
                double ea = i > 1 ? Math.abs((xr - prevXR) / xr) * 100 : 0;

                xList.add(xr);
                yList.add(fxr);

                tableData.add(new Object[]{i, formatNumber(xl), formatNumber(xr), formatNumber(xu), formatNumber(fxl), formatNumber(fxr), formatNumber(ea), formatNumber(product), product < 0 ? "Root found" : ""});

                outputArea.append(i + "\t"
                        + String.format("%.4f", xl) + "\t"
                        + String.format("%.4f", xr) + "\t"
                        + String.format("%.4f", xu) + "\t"
                        + String.format("%.4f", fxl) + "\t"
                        + String.format("%.4f", fxr) + "\t"
                        + String.format("%.4f", ea) + "\t"
                        + String.format("%.4f", product) + "\n");

                if (product < 0)
                    xu = xr;
                else
                    xl = xr;

                prevXR = xr;

                if (ea < tolerance && i > 1) {
                    outputArea.append("\nTolerance reached.");
                    break;
                }
            }
        }

        else if (method.equals("Regula Falsi")) {
            outputArea.append("i\tXL\tXR\tXU\tf(XL)\tf(XR)\tEa\tProduct\tRemarks\n");

            double prevXR = 0;

            for (int i = 1; i <= 100; i++) {

                double fxl = f(xl);
                double fxu = f(xu);
                double xr = (xl * fxu - xu * fxl) / (fxu - fxl);
                double fxr = f(xr);
                double product = fxl * fxr;
                double ea = i > 1 ? Math.abs((xr - prevXR) / xr) * 100 : 0;

                xList.add(xr);
                yList.add(fxr);

                String remarks = "";

                if (product < 0)
                    remarks = "Replace XU";
                else if (product > 0)
                    remarks = "Replace XL";

                if (Math.abs(fxr) < tolerance)
                    remarks = "Root Found";

                tableData.add(new Object[]{
                    i,
                    formatNumber(xl),
                    formatNumber(xr),
                    formatNumber(xu),
                    formatNumber(fxl),
                    formatNumber(fxr),
                    formatNumber(ea),
                    formatNumber(product),
                    remarks
                });

                outputArea.append(i + "\t"
                        + String.format("%.4f", xl) + "\t"
                        + String.format("%.4f", xr) + "\t"
                        + String.format("%.4f", xu) + "\t"
                        + String.format("%.4f", fxl) + "\t"
                        + String.format("%.4f", fxr) + "\t"
                        + String.format("%.4f", ea) + "\t"
                        + String.format("%.4f", product) + "\n");

                if (product < 0)
                    xu = xr;
                else
                    xl = xr;

                prevXR = xr;

                if (ea < tolerance && i > 1)
                    break;
            }
        }

        else if (method.equals("Newton Raphson")) {
            outputArea.append("i\tXL\tXR\tXU\tf(XL)\tf(XR)\tEa\tProduct\tRemarks\n");

            double x0 = xl;
            double prevXR = 0;

            for (int i = 1; i <= 100; i++) {

                double xr = x0 - f(x0) / df(x0);
                double fxr = f(xr);
                double ea = i > 1 ? Math.abs((xr - prevXR) / xr) * 100 : 0;

                xList.add(xr);
                yList.add(fxr);

                String remarks = "";

                if (Math.abs(fxr) < tolerance)
                    remarks = "Converged";
                else if (fxr > 0)
                    remarks = "Positive Error";
                else
                    remarks = "Negative Error";

               tableData.add(new Object[]{
                    i,
                    formatNumber(x0),
                    formatNumber(xr),
                    formatNumber(df(x0)),
                    formatNumber(f(x0)),
                    formatNumber(fxr),
                    formatNumber(ea),
                    formatNumber(f(x0) * fxr),
                    remarks
                });

                outputArea.append(i + "\t"
                    + String.format("%.6f", x0) + "\t"
                    + String.format("%.6f", xr) + "\t"
                    + String.format("%.6f", xr) + "\t"
                    + String.format("%.6f", f(x0)) + "\t"
                    + String.format("%.6f", fxr) + "\t"
                    + String.format("%.6f", ea) + "\t"
                    + String.format("%.6f", (f(x0) * fxr)) + "\t"
                    + (Math.abs(fxr) < tolerance ? "Converged" : "") + "\n");

                if (Math.abs(fxr) < 0.0001)
                    break;

                x0 = xr;
                prevXR = xr;
            }
        }

        else if (method.equals("Secant")) {
            outputArea.append("i\tXL\tXR\tXU\tf(XL)\tf(XR)\tEa\tProduct\tRemarks\n");

            double x0 = xl;
            double x1 = xu;
            double prevXR = 0;

            for (int i = 1; i <= 100; i++) {

                double fxr = f(x1) * (x1 - x0) / (f(x1) - f(x0));
                double xr = x1 - fxr;
                double fxr2 = f(xr);
                double ea = i > 1 ? Math.abs((xr - prevXR) / xr) * 100 : 0;

                xList.add(xr);
                yList.add(fxr2);

                String remarks = "";

                if (Math.abs(fxr2) < tolerance)
                    remarks = "Converged";
                else if (fxr2 > 0)
                    remarks = "Positive Error";
                else
                    remarks = "Negative Error";

                tableData.add(new Object[]{
                    i,
                    formatNumber(x0),                  // XL
                    formatNumber(xr),                  // XR
                    formatNumber(x1),                  // XU
                    formatNumber(f(x0)),               // f(XL)
                    formatNumber(fxr2),                // f(XR)
                    formatNumber(ea),                  // Ea
                    formatNumber(f(x0) * fxr2),        // Product
                    remarks
                });

                outputArea.append(i + "\t"
                    + String.format("%.6f", x0) + "\t"
                    + String.format("%.6f", xr) + "\t"
                    + String.format("%.6f", df(x0)) + "\t"
                    + String.format("%.6f", f(x0)) + "\t"
                    + String.format("%.6f", fxr) + "\t"
                    + String.format("%.6f", ea) + "\t"
                    + String.format("%.6f", (f(x0) * fxr)) + "\t"
                    + (Math.abs(fxr2) < tolerance ? "Converged" : "") + "\n");

                if (Math.abs(fxr2) < 0.0001)
                    break;

                x0 = x1;
                x1 = xr;
                prevXR = xr;
            }
        }

        // Display table
        String[] columnNames = {"i", "XL", "XR", "XU", "f(XL)", "f(XR)", "Ea", "Product", "Remarks"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (Object[] row : tableData) {
            model.addRow(row);
        }
        resultsTable.setModel(model);

        // Update graph
        graphPanel.setPoints(xList.stream().mapToDouble(Double::doubleValue).toArray(),
                yList.stream().mapToDouble(Double::doubleValue).toArray());
    }

    private String formatNumber(double num) {
        if (num == 0) return "";
        return String.format("%.6f", num);
    }

    // ================= MATRIX =================

    double[][] parseMatrix(String text) {

        String[] rows = text.split("\\n");
        int r = rows.length;
        int c = rows[0].trim().split("\\s+").length;

        double[][] matrix = new double[r][c];

        for (int i = 0; i < r; i++) {
            String[] values = rows[i].trim().split("\\s+");

            for (int j = 0; j < c; j++) {
                matrix[i][j] = Double.parseDouble(values[j]);
            }
        }

        return matrix;
    }

    String matrixToString(double[][] A) {

        StringBuilder sb = new StringBuilder();

        for (double[] row : A) {
            for (double val : row) {
                sb.append(String.format("%10.4f", val));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    double[][] add(double[][] A, double[][] B) {

        int r = A.length;
        int c = A[0].length;

        double[][] C = new double[r][c];

        for (int i = 0; i < r; i++)
            for (int j = 0; j < c; j++)
                C[i][j] = A[i][j] + B[i][j];

        return C;
    }

    double[][] multiply(double[][] A, double[][] B) {

        int r = A.length;
        int c = B[0].length;

        double[][] C = new double[r][c];

        for (int i = 0; i < r; i++)
            for (int j = 0; j < c; j++)
                for (int k = 0; k < B.length; k++)
                    C[i][j] += A[i][k] * B[k][j];

        return C;
    }

    double[][] transpose(double[][] A) {

        int r = A.length;
        int c = A[0].length;

        double[][] T = new double[c][r];

        for (int i = 0; i < r; i++)
            for (int j = 0; j < c; j++)
                T[j][i] = A[i][j];

        return T;
    }

    double determinant(double[][] A) {

        if (A.length == 1)
            return A[0][0];

        double det = 0;

        for (int i = 0; i < A.length; i++) {
            det += Math.pow(-1, i) * A[0][i] * determinant(minor(A, 0, i));
        }

        return det;
    }

    double[][] minor(double[][] A, int row, int col) {

        int n = A.length;

        double[][] M = new double[n - 1][n - 1];

        int r = 0;

        for (int i = 0; i < n; i++) {

            if (i == row)
                continue;

            int c = 0;

            for (int j = 0; j < n; j++) {

                if (j == col)
                    continue;

                M[r][c++] = A[i][j];
            }

            r++;
        }

        return M;
    }

    double[][] adjoint(double[][] A) {

        int n = A.length;

        double[][] adj = new double[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                adj[j][i] = Math.pow(-1, i + j) * determinant(minor(A, i, j));

        return adj;
    }

    double[][] inverse(double[][] A) {

        double det = determinant(A);
        double[][] adj = adjoint(A);

        int n = A.length;

        double[][] inv = new double[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                inv[i][j] = adj[i][j] / det;

        return inv;
    }

    double[][] solveEquation(double[][] A, double[][] B) {

    double[][] invA = inverse(A);

    return multiply(invA, B);
    }

    void computeMatrix() {

        try {

            double[][] A = parseMatrix(matrixA.getText());
            String op = (String) matrixOp.getSelectedItem();

            if (op.equals("Addition")) {

                double[][] B = parseMatrix(matrixB.getText());
                matrixResult.setText(matrixToString(add(A, B)));
            }

            else if (op.equals("Multiplication")) {

                double[][] B = parseMatrix(matrixB.getText());
                matrixResult.setText(matrixToString(multiply(A, B)));
            }

            else if (op.equals("Transpose")) {
                matrixResult.setText(matrixToString(transpose(A)));
            }

            else if (op.equals("Determinant")) {
                matrixResult.setText("Determinant = " + determinant(A));
            }

            else if (op.equals("Inverse")) {
                matrixResult.setText(matrixToString(inverse(A)));
            }

            else if (op.equals("Adjoint")) {
                matrixResult.setText(matrixToString(adjoint(A)));
            }

            else if (op.equals("Power")) {
                matrixResult.setText(matrixToString(multiply(A, A)));
            }

            else if (op.equals("Equation")) {

                double[][] B = parseMatrix(matrixB.getText());

                matrixResult.setText(
                    matrixToString(solveEquation(A, B))
                );
            }

        } catch (Exception ex) {
            matrixResult.setText("Invalid Matrix Input");
        }
    }

    // ================= MAIN =================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new NumericalMethodsApp();
            }
        });
    }
}