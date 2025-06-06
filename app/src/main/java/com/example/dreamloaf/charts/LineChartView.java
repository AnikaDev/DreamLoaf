package com.example.dreamloaf.charts;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import java.util.*;

public class LineChartView extends View {
    private Map<String, Map<String, Integer>> lineChartData = new HashMap<>();
    private Map<String, Integer> pieChartData = new HashMap<>();

    private int width, height;
    private int padding = 70;
    private float maxValue = 1;
    private int chartHeight;

    private Paint linePaint, pointPaint, textPaint, axisPaint, gridPaint, fillPaint;



    private int[] colors = {
            Color.parseColor("#FF6200EE"),
            Color.parseColor("#FF03DAC5"),
            Color.parseColor("#FF018786"),
            Color.parseColor("#FFBB86FC"),
            Color.parseColor("#FF3700B3"),
            Color.parseColor("#FFFF5722"),
            Color.parseColor("#FFE91E63"),
            Color.parseColor("#FF8BC34A"),
            Color.parseColor("#FF795548"),
            Color.parseColor("#FF9C27B0"),
            Color.parseColor("#FF3F51B5"),
            Color.parseColor("#FF00BCD4"),
            Color.parseColor("#FFCDDC39"),
            Color.parseColor("#FFFF9800"),
            Color.parseColor("#FF607D8B")
    };

    public LineChartView(Context context) {
        super(context);
        init();
    }

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(4f);

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(28f);

        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setColor(Color.BLACK);
        axisPaint.setStrokeWidth(3f);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.LTGRAY);
        gridPaint.setStrokeWidth(1f);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
    }

    public void setLineData(Map<String, Map<String, Integer>> lineData) {
        this.lineChartData = lineData != null ? lineData : new HashMap<>();
        calculateMaxValue();
        invalidate();
    }

    public void setPieData(Map<String, Integer> pieData) {
        this.pieChartData = pieData != null ? pieData : new HashMap<>();
        invalidate();
    }

    private void calculateMaxValue() {
        maxValue = 1;
        for (Map<String, Integer> productData : lineChartData.values()) {
            for (int value : productData.values()) {
                if (value > maxValue) maxValue = value;
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;

        int totalPaddings = padding * 3;
        int legendSpace = 0;
        this.chartHeight = (height - totalPaddings - legendSpace) / 2;

        if (chartHeight < 100) chartHeight = 100;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        if (lineChartData.isEmpty() && pieChartData.isEmpty()) {
            drawNoDataMessage(canvas);
            return;
        }

        if (height <= 3 * padding || width <= 2 * padding) {
            Log.w("LineChart", "Not enough space to draw chart");
            return;
        }

        int currentTop = padding;

        if (!pieChartData.isEmpty()) {
            drawPieChart(canvas, padding, currentTop, width - padding, currentTop + chartHeight);
            currentTop += chartHeight + padding;
        }

        int remainingHeight = height - currentTop - padding;
        int lineChartHeight = Math.min(remainingHeight, chartHeight);

        if (!lineChartData.isEmpty()) {
            drawLineChart(canvas,
                    padding,
                    currentTop,
                    width - padding,
                    currentTop + lineChartHeight);
        }
    }

    private void drawPieChart(Canvas canvas, float left, float top, float right, float bottom) {
        int total = getTotalPieValue();
        if (total == 0) return;

        float centerX = (left + right) / 2;
        float centerY = (top + bottom) / 2;
        float radius = Math.min(right - left, bottom - top) / 2 - padding/2;

        float startAngle = 0;
        int colorIndex = 0;
        for (Map.Entry<String, Integer> entry : pieChartData.entrySet()) {
            float sweepAngle = 360f * entry.getValue() / total;

            fillPaint.setColor(colors[colorIndex % colors.length]);
            canvas.drawArc(new RectF(centerX - radius, centerY - radius,
                            centerX + radius, centerY + radius),
                    startAngle, sweepAngle, true, fillPaint);

            if (sweepAngle > 15) {
                float percent = 100f * entry.getValue() / total;
                float midAngle = startAngle + sweepAngle / 2;
                float textX = centerX + (radius * 0.7f) * (float)Math.cos(Math.toRadians(midAngle));
                float textY = centerY + (radius * 0.7f) * (float)Math.sin(Math.toRadians(midAngle));

                textPaint.setColor(Color.WHITE);
                canvas.drawText(String.format("%.1f%%", percent), textX, textY, textPaint);
            }

            startAngle += sweepAngle;
            colorIndex++;
        }

        fillPaint.setStyle(Paint.Style.STROKE);
        fillPaint.setColor(Color.BLACK);
        fillPaint.setStrokeWidth(2f);
        canvas.drawCircle(centerX, centerY, radius, fillPaint);
        fillPaint.setStyle(Paint.Style.FILL);
    }

    private void drawLineChart(Canvas canvas, float left, float top, float right, float bottom) {
        float axisPadding = 20f;
        float chartBottom = bottom - axisPadding;

        canvas.drawLine(left, top, left, chartBottom, axisPaint);
        canvas.drawLine(left, chartBottom, right, chartBottom, axisPaint);
        int gridLines = 5;
        for (int i = 0; i <= gridLines; i++) {
            float y = chartBottom - i * (chartBottom - top) / gridLines;
            canvas.drawLine(left, y, right, y, gridPaint);
            canvas.drawText(String.valueOf((int)(maxValue * i / gridLines)),
                    left - 10, y + 5, textPaint);
        }

        Set<String> dates = getAllDates();
        if (dates.isEmpty()) return;

        List<String> sortedDates = new ArrayList<>(dates);
        Collections.sort(sortedDates);

        float xStep = (right - left) / (sortedDates.size() - 1);

        int colorIndex = 0;
        for (Map.Entry<String, Map<String, Integer>> entry : lineChartData.entrySet()) {
            drawDataLine(canvas, entry.getValue(), sortedDates, left, top, chartBottom, xStep,
                    colors[colorIndex % colors.length]);
            colorIndex++;
        }

        drawDateLabels(canvas, sortedDates, left, chartBottom, xStep);
    }

    private void drawDataLine(Canvas canvas, Map<String, Integer> data, List<String> dates,
                              float left, float top, float bottom, float xStep, int color) {
        linePaint.setColor(color);
        pointPaint.setColor(color);

        Path path = new Path();
        boolean firstPoint = true;

        for (int i = 0; i < dates.size(); i++) {
            String date = dates.get(i);
            if (data.containsKey(date)) {
                float x = left + i * xStep;
                float y = bottom - (data.get(date) / maxValue) * (bottom - top);

                canvas.drawCircle(x, y, 8, pointPaint);

                if (firstPoint) {
                    path.moveTo(x, y);
                    firstPoint = false;
                } else {
                    path.lineTo(x, y);
                }
            }
        }

        canvas.drawPath(path, linePaint);
    }

    private void drawDateLabels(Canvas canvas, List<String> dates, float left, float bottom, float xStep) {
        textPaint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < dates.size(); i += 2) {
            String date = dates.get(i);
            float x = left + i * xStep;
            canvas.drawText(formatDate(date), x, bottom + 35, textPaint);
        }
    }

    private Set<String> getAllDates() {
        Set<String> dates = new TreeSet<>();
        for (Map<String, Integer> data : lineChartData.values()) {
            dates.addAll(data.keySet());
        }
        return dates;
    }

    private int getTotalPieValue() {
        int total = 0;
        for (int value : pieChartData.values()) {
            total += value;
        }
        return total;
    }

    private String formatDate(String dateStr) {
        try {
            String[] parts = dateStr.split("-");
            if (parts.length >= 3) {
                return parts[2] + "." + parts[1];
            }
            return dateStr;
        } catch (Exception e) {
            return dateStr;
        }
    }

    private void drawNoDataMessage(Canvas canvas) {
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(36f);
        canvas.drawText("Нет данных для отображения",
                width / 2, height / 2, textPaint);
    }
}