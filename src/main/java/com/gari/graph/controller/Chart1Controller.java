package com.gari.graph.controller;

import com.gari.graph.pojo.PointsData;
import com.gari.graph.pojo.TemporaryPoint;
import com.gari.graph.utils.JacksonUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class Chart1Controller implements Initializable {

    @FXML
    private LineChart<String, Number> altChart;

    @FXML
    private LineChart<String, Number> latChart;

    @FXML
    private LineChart<String, Number> longChart;
    @FXML
    private LineChart<String, Number> vSpeedChart;
    @FXML
    private LineChart<String, Number> hSpeedChart;
    @FXML
    private LineChart<String, Number> courseChart;
    @FXML
    private LineChart<String, Number> pitchChart;
    @FXML
    private LineChart<String, Number> rollChart;
    @FXML
    private LineChart<String, Number> batteryChart;

    private List<LineChart<String, Number>> charts = new ArrayList<>();

    @FXML
    private Button minusButton;
    @FXML
    private Button plusButton;
    @FXML
    private TextField synchroText;

    @FXML
    private TextArea inputPointsArea;

    @FXML
    private Button applyButton;

    @FXML
    private Button clearButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Init List of Charts
        charts = Arrays.asList(altChart, latChart, longChart, vSpeedChart, hSpeedChart, courseChart, pitchChart, rollChart, batteryChart);

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Test Line");
        series1.getData().add(new XYChart.Data<>("1", 20));
        series1.getData().add(new XYChart.Data<>("2", 100));
        series1.getData().add(new XYChart.Data<>("3", 80));
        series1.getData().add(new XYChart.Data<>("4", 180));
        series1.getData().add(new XYChart.Data<>("5", 20));
        series1.getData().add(new XYChart.Data<>("6", -10));
        altChart.getData().add(series1);

        String testSimulated = "";

        inputPointsArea.setText(testSimulated);

        synchroText.setText("0");

        // Clear All text in two Text Fields for points JSON
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                inputPointsArea.setText("");
            }
        });

        plusButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                int synchroSeconds = StringUtils.isBlank(synchroText.getText()) ? 0 : Integer.parseInt(synchroText.getText());

                updateEvent(synchroSeconds + 1);
            }
        });

        minusButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                int synchroSeconds = StringUtils.isBlank(synchroText.getText()) ? 0 : Integer.parseInt(synchroText.getText());

                updateEvent(synchroSeconds - 1);
            }
        });

        // Get Json of Points from 2 text fields and draw Chars of REAL and SIM telemetry data
        applyButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                int synchroSeconds = StringUtils.isBlank(synchroText.getText()) ? 0 : Integer.parseInt(synchroText.getText());

                updateEvent(synchroSeconds);
            }
        });
        log.info("TEST LOG {} ", "First Part");
    }

    private void updateEvent(int inputSynchroSeconds) {

        synchroText.setText(String.valueOf(inputSynchroSeconds));

        // Clear all data from all Charts
        charts.forEach(chart -> chart.getData().clear());

        // Read JSONs of Points from Text fields
        String jsonPointsData = inputPointsArea.getText();

        // Update Charts
        updateChartDataTheeLines(inputSynchroSeconds, altChart, jsonPointsData, TemporaryPoint::getAltitude, TemporaryPoint::getAltitude, TemporaryPoint::getReliefHeight, 1d);

        updateChartData(inputSynchroSeconds, latChart, jsonPointsData, TemporaryPoint::getLatitude, 0.000001d);
        updateChartData(inputSynchroSeconds, longChart, jsonPointsData, TemporaryPoint::getLongitude, 0.000001d);
        updateChartData(inputSynchroSeconds, vSpeedChart, jsonPointsData, TemporaryPoint::getVerticalSpeed, 1d);
        updateChartData(inputSynchroSeconds, hSpeedChart, jsonPointsData, TemporaryPoint::getSpeed, 1d);
        updateChartData(inputSynchroSeconds, courseChart, jsonPointsData, TemporaryPoint::getCourse, 1d);
        updateChartData(inputSynchroSeconds, pitchChart, jsonPointsData, TemporaryPoint::getPitch, 1d);
        updateChartData(inputSynchroSeconds, rollChart, jsonPointsData, TemporaryPoint::getRoll, 1d);
        updateChartData(inputSynchroSeconds, batteryChart, jsonPointsData, TemporaryPoint::getChargePercents, 1d);
    }


    // Parse Data for Charts
    private void updateChartData(int synchroSeconds, LineChart<String, Number> chart, String jsonDataPoints, Function<TemporaryPoint, Number> parameter, Double tickValue) {
        if (StringUtils.isBlank(jsonDataPoints)) {
            log.error("Json Data is EMPTY");
            return;
        }

        PointsData pointsData = getPointsDataFromJson(jsonDataPoints);
        if (Objects.isNull(pointsData)) {
            log.error("PointsData not converted");
            return;
        }

        List<TemporaryPoint> simPoints = pointsData.getSimPoints();
        List<TemporaryPoint> realPoints = pointsData.getRealPoints();

        if (CollectionUtils.isEmpty(simPoints) || CollectionUtils.isEmpty(realPoints)) {
            log.error("Some of Points Data is Empty [SIM or REAL]");
            return;
        }

        // Little synchronize
        TemporaryPoint firstSimPoint = simPoints.get(0);

        for (int i = 0; i <= synchroSeconds; i++) {
            simPoints.add(0, firstSimPoint);
        }

        AtomicInteger countSim = new AtomicInteger(1);
        List<XYChart.Data<String, Number>> simData = simPoints.stream().map(point -> new XYChart.Data<>(String.format("%s", countSim.getAndIncrement()), parameter.apply(point))).collect(Collectors.toList());

        AtomicInteger countReal = new AtomicInteger(1);
        List<XYChart.Data<String, Number>> realData = realPoints.stream().map(point -> new XYChart.Data<>(String.format("%s", countReal.getAndIncrement()), parameter.apply(point))).collect(Collectors.toList());

        updateChart(chart, simData, realData, tickValue);
    }


    // Create CHART View
    private void updateChart(LineChart<String, Number> chart, List<XYChart.Data<String, Number>> simData, List<XYChart.Data<String, Number>> realData, double tickValue) {
        double minValueSim = simData.stream().mapToDouble(point -> (double) point.getYValue()).min().orElse(0) - tickValue * 10.;
        double maxValueSim = simData.stream().mapToDouble(point -> (double) point.getYValue()).max().orElse(0) + tickValue * 10.;
        double minValueReal = realData.stream().mapToDouble(point -> (double) point.getYValue()).min().orElse(0) - tickValue * 10.;
        double maxValueReal = realData.stream().mapToDouble(point -> (double) point.getYValue()).max().orElse(0) + tickValue * 10.;

        NumberAxis yNumberAx = (NumberAxis) chart.getYAxis();
        yNumberAx.setAutoRanging(false);
        yNumberAx.setLowerBound(Math.min(minValueSim, minValueReal));
        yNumberAx.setUpperBound(Math.max(maxValueSim, maxValueReal));
        yNumberAx.setTickUnit(tickValue);


        XYChart.Series<String, Number> seriesSim = new XYChart.Series<>();
        seriesSim.setName("SIM");
        seriesSim.getData().addAll(simData);

        chart.getData().add(seriesSim);

        XYChart.Series<String, Number> seriesReal = new XYChart.Series<>();
        seriesReal.setName("REAL");
        seriesReal.getData().addAll(realData);

        chart.getData().add(seriesReal);

        Platform.runLater(()
                -> {

            Set<Node> nodes = chart.lookupAll(".series" + 1);
            for (Node n : nodes) {
                n.setStyle("-fx-background-color: blue, white;\n"
                        + "    -fx-background-insets: 0, 2;\n"
                        + "    -fx-background-radius: 5px;\n"
                        + "    -fx-padding: 5px;");
            }

            Set<Node> nodes1 = chart.lookupAll(".series" + 0);
            for (Node n : nodes1) {
                n.setStyle("-fx-background-color: red, white;\n"
                        + "    -fx-background-insets: 0, 2;\n"
                        + "    -fx-background-radius: 5px;\n"
                        + "    -fx-padding: 5px;");
            }

            seriesReal.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: blue; -fx-background-color: blue, white;");
            seriesSim.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: red;");
        });
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Parse Data for Charts
    private void updateChartDataTheeLines(int synchroSeconds, LineChart<String, Number> chart, String jsonDataPoints, Function<TemporaryPoint, Number> parameter1, Function<TemporaryPoint, Number> parameter2, Function<TemporaryPoint, Number> parameter3, Double tickValue) {
        if (StringUtils.isBlank(jsonDataPoints)) {
            log.error("Json Data is EMPTY");
            return;
        }

        PointsData pointsData = getPointsDataFromJson(jsonDataPoints);
        if (Objects.isNull(pointsData)) {
            log.error("PointsData not converted");
            return;
        }

        List<TemporaryPoint> simPoints = pointsData.getSimPoints();
        List<TemporaryPoint> realPoints = pointsData.getRealPoints();

        if (CollectionUtils.isEmpty(simPoints) || CollectionUtils.isEmpty(realPoints)) {
            log.error("Some of Points Data is Empty [SIM or REAL]");
            return;
        }

        // Little synchronize
        TemporaryPoint firstSimPoint = simPoints.get(0);

        for (int i = 0; i <= synchroSeconds; i++) {
            simPoints.add(0, firstSimPoint);
        }

        AtomicInteger countSim = new AtomicInteger(1);
        List<XYChart.Data<String, Number>> simData = simPoints.stream().map(point -> new XYChart.Data<>(String.format("%s", countSim.getAndIncrement()), parameter1.apply(point))).collect(Collectors.toList());

        AtomicInteger countReal = new AtomicInteger(1);
        List<XYChart.Data<String, Number>> realData = realPoints.stream().map(point -> new XYChart.Data<>(String.format("%s", countReal.getAndIncrement()), parameter2.apply(point))).collect(Collectors.toList());

        AtomicInteger additionalCount = new AtomicInteger(1);
        List<XYChart.Data<String, Number>> additionalData = realPoints.stream().map(point -> new XYChart.Data<>(String.format("%s", additionalCount.getAndIncrement()), parameter3.apply(point))).collect(Collectors.toList());


        updateChartThreeLines(chart, simData, realData, additionalData, tickValue);
    }


    // Create CHART View
    private void updateChartThreeLines(LineChart<String, Number> chart, List<XYChart.Data<String, Number>> simData, List<XYChart.Data<String, Number>> realData, List<XYChart.Data<String, Number>> additionalData, double tickValue) {
        double minValueSim = simData.stream().mapToDouble(point -> (double) point.getYValue()).min().orElse(0) - tickValue * 10.;
        double maxValueSim = simData.stream().mapToDouble(point -> (double) point.getYValue()).max().orElse(0) + tickValue * 10.;
        double minValueReal = realData.stream().mapToDouble(point -> (double) point.getYValue()).min().orElse(0) - tickValue * 10.;
        double maxValueReal = realData.stream().mapToDouble(point -> (double) point.getYValue()).max().orElse(0) + tickValue * 10.;

        NumberAxis yNumberAx = (NumberAxis) chart.getYAxis();
        yNumberAx.setAutoRanging(false);
        yNumberAx.setLowerBound(Math.min(minValueSim, minValueReal));
        yNumberAx.setUpperBound(Math.max(maxValueSim, maxValueReal));
        yNumberAx.setTickUnit(tickValue);


        XYChart.Series<String, Number> seriesSim = new XYChart.Series<>();
        seriesSim.setName("SIM");
        seriesSim.getData().addAll(simData);
        chart.getData().add(seriesSim);

        XYChart.Series<String, Number> seriesReal = new XYChart.Series<>();
        seriesReal.setName("REAL");
        seriesReal.getData().addAll(realData);
        chart.getData().add(seriesReal);

        XYChart.Series<String, Number> seriesAdditional = new XYChart.Series<>();
        seriesAdditional.setName("Additional");
        seriesAdditional.getData().addAll(additionalData);
        chart.getData().add(seriesAdditional);


        Platform.runLater(()
                -> {
            Set<Node> nodes2 = chart.lookupAll(".series" + 2);
            for (Node n : nodes2) {
                n.setStyle("-fx-background-color: black, black;\n"
                        + "    -fx-background-insets: 0, 2;\n"
                        + "    -fx-background-radius: 1px;\n"
                        + "    -fx-padding: 1px;");
            }

            Set<Node> nodes1 = chart.lookupAll(".series" + 1);
            for (Node n : nodes1) {
                n.setStyle("-fx-background-color: blue, white;\n"
                        + "    -fx-background-insets: 0, 2;\n"
                        + "    -fx-background-radius: 5px;\n"
                        + "    -fx-padding: 5px;");
            }

            Set<Node> nodes = chart.lookupAll(".series" + 0);
            for (Node n : nodes) {
                n.setStyle("-fx-background-color: red, white;\n"
                        + "    -fx-background-insets: 0, 2;\n"
                        + "    -fx-background-radius: 5px;\n"
                        + "    -fx-padding: 5px;");
            }

            seriesReal.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: blue;");
            seriesSim.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: red;");
            seriesAdditional.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: black;");
        });
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private PointsData getPointsDataFromJson(String jsonDataPoints) {
        PointsData pointsData = null;
        if (StringUtils.isNotBlank(jsonDataPoints)) {
            try {
                pointsData = JacksonUtils.fromJson(PointsData.class, jsonDataPoints);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pointsData;
    }

}
