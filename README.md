# weight-limit-chart
Android weight limit chart with color area. Different color area for upper limit, lower limit and normal values.

LimitLineChart mChart = (LimitLineChart) findViewById(R.id.chart1);

  mChart.upperLimit = 70.5f;  // Start of upper limit
  
  mChart.lowerLimit = 50f;    // End of lower limit
  
  mChart.currentValue = 0f;
  
  mChart.drawGraph();
  
For more detail visit web site: http://wisdomitsol.com/blog/android/android-weight-limit-chart
