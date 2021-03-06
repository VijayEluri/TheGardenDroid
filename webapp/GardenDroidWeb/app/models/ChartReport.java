package models;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * Supports a friendly (hopefully) way to set ups a basic <b>flot</b> chart. THe goal is not to reproduce everything, if a user
 * Wishes to have full control they can build a Script Chart and generate a chart fully specifying the flot Chart.
 * 
 * Note the resulting toChart output should follow the following pattern: {data:[{...},{...}],options:{}}
 * 
 * @author leeclarke
 */
public class ChartReport {
	private static final Logger logger = Logger.getLogger(ChartReport.class);
	
	public ArrayList<Point> dataSet1 = new ArrayList<Point>();
	public ArrayList<Point> dataSet2 = new ArrayList<Point>();

	/**
	 * options should contain <String, Object|HashMap<String,String>> but thats not possible..so its enforced by
	 * setters.
	 */
	public HashMap<String, Object> options = new HashMap<String, Object>();
	
	public String label1 = "";
	public String label2 = "";

	/**
	 * Default Constructor ensures that there are 2 dataSets which are the max amount flot seems to support at this
	 * time.
	 */
	public ChartReport() {

	}

	/**
	 * Generates the output HTML for displaying a chart. Same as calling toString().
	 * 
	 * @return
	 */
	public String toChart() {
		StringBuilder sb = new StringBuilder("{data:[");
		// data write out
		
		sb.append(writeSingleDataSet(this.label1,dataSet1));
		String d2 = writeSingleDataSet(this.label2, this.dataSet2);
        if(d2.length()>0) {
            sb.append(", ").append(d2);
        }
	    // end data
        sb.append("]");
        
		// Options
		sb.append(writeOptions());
		
		sb.append("}");
		return sb.toString();
	}


	/**
	 * Writes out options in JS notation.
	 * @return
	 */
	public Object writeOptions() {
		StringBuilder sb = new StringBuilder();
		setDefaultOptions();
		logger.warn("Options=="+this.options);
		
		sb.append(",options:{");
		
		sb.append(writeMap(this.options));
		
		sb.append("}");
		
		return sb.toString();
	}
	
	/**
	 * Ensure that Defaults get set if there is an absence of the option settings.
	 */
	protected void setDefaultOptions(){
		if (this.options.isEmpty() || !(this.options.containsKey("grid"))) {
			HashMap<String, Object> gridDefaultColor = new HashMap<String, Object>(); 
			gridDefaultColor.put("color", "#B8C569");
			this.options.put("grid", gridDefaultColor);
		} else if(this.options.containsKey("grid")){
			try {
				HashMap<String, Object> grid = (HashMap<String, Object>) this.options.get("grid");
				if(!grid.containsKey("color")) { //Enforces color default, this might be better done though a css setting..
					grid.put("color","#B8C569");
				}}
			catch (Exception e) {
				logger.info("Error retrieving grid values, this means the use made soem sort of error in the code but can't pass that up at this time. " + e);
			}
		}
	}

	/**
	 * Converts any map to JSON
	 * @param theMap
	 * @return
	 */
	private String writeMap(HashMap<String, Object> theMap) {
		StringBuilder sb = new StringBuilder();
		boolean firstPass=true;
		for(String key : theMap.keySet()) {
			logger.warn("Map key="+key);
			
			if(!firstPass)
				sb.append(",");
			sb.append(key).append(":");
			Object val = theMap.get(key);
			if(val instanceof HashMap){
				sb.append("{").append(writeMap((HashMap<String, Object>) val)).append("}");
			}
			else if(val instanceof Boolean || val.getClass().isAssignableFrom(Number.class)){
				sb.append(val);
			}
			else{
				sb.append("\"").append(val).append("\"");
			}
			firstPass=false;
		}
		return sb.toString();
	}

	/**
	 * Writes a single dataset to JS notation.
	 * @param dataSet12
	 * @return - blank if no data to write.
	 */
	public String writeSingleDataSet(String label, ArrayList<Point> dataSet) {
        StringBuilder sb = new StringBuilder();
        if (dataSet.size() > 0) {
        	sb.append("{");
        	if(label != null && label.length()>0){
        		sb.append("label:\"").append(label).append("\", ");
        	}
        		
            sb.append("data:[");
            boolean isFirst = true;
            for (Point point : dataSet) {
                if (!isFirst)
                    sb.append(",");
                sb.append(point.toString());
                isFirst = false;
            }
            sb.append("]");
            sb.append("}");
        }
        return sb.toString();
    }

	@Override
	public String toString() {
		return this.toChart();
	}

	public Point getPoint(Number x, Number y) {
        return new Point(x,y);
    }
	
	/**
	 * Contains the data for a single point on the Chart.
	 * 
	 */
	public class Point {

		public Number xValue;
		public Number yValue;

		public Point(Number xValue, Number yValue) {
			this.xValue = xValue;
			this.yValue = yValue;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("[");
			sb.append(this.xValue).append(",").append(this.yValue).append("]");
			return sb.toString();
		}
	}
}
