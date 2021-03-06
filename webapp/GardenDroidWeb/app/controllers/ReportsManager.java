/**
 * The GardenDroid, a self monitoring and reporting mini-greenhouse.
 *
 * Copyright (c) 2010-2011 Lee Clarke
 *
 * LICENSE:
 *
 * This file is part of TheGardenDroid (https://github.com/leeclarke/TheGardenDroid).
 *
 * TheGardenDroid is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * TheGardenDroid is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with TheGardenDroid.  If not, see
 * <http://www.gnu.org/licenses/>.
 *
 */
package controllers;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;

import models.ObservationData;
import models.Plant;
import models.PlantData;
import models.ReportType;
import models.ReportUserScript;
import models.SensorData;
import models.SensorType;
import models.TempSensorData;
import models.UserDataType;
import play.Play;
import play.data.validation.Required;
import play.db.jpa.JPABase;
import play.exceptions.CompilationException;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

/**
 * Central controller for viewing Reports.
 * @author leeclarke
 */
@With(Secure.class)
public class ReportsManager  extends Controller{
	static Logger logger = Logger.getLogger(ReportsManager.class);
	
	@Before
	static void addDefaults() {
	    renderArgs.put("appTitle", Play.configuration.getProperty("droid.title"));
	    renderArgs.put("appBaseline", Play.configuration.getProperty("droid.baseline"));
	    //Added Help URL as a property so it can be cahnged easily in the future if needed.
	    renderArgs.put("helpLink", Play.configuration.getProperty("droid.helpLink"));
	}
	
	/**
	 * Supports Edit form
	 * @param id
	 */
	public static void editUserScript(Long id) {
		List<Plant> plantings = Plant.find("isActive = ?", true).fetch();
		List<ReportType> reportTypes = Arrays.asList(ReportType.values());
		if(id != null) {
			ReportUserScript script = ReportUserScript.findById(id);
			if(script != null)
				render(plantings,script, reportTypes);
		}
		
		render(plantings, reportTypes);
		
	}
	
	public static void saveUserScript(Long id, @Required(message="Must provide a name.") String name, String description, @Required(message="You need to provide a script!") String scriptBody, Date startDate, Date endDate, Long plantDataId, boolean activeOnlyPlantings, ReportType reportType, String reportField) {
		logger.warn("##### ENTER save id="+id);
		if(params._contains("deleteScript")){
			logger.warn("##### got DEL req");
			deleteUserScript(id);
		}
		ReportUserScript script;
		Plant plant = null;
		if(plantDataId != null && plantDataId >-1)
			plant = Plant.findById(plantDataId);
		if(id != null && id > -1) {
			logger.warn("Saving id="+id);
			script = ReportUserScript.findById(id);
			logger.warn("Got script, do update");
			if(script != null) {
				script.name = name;
				script.description = description;
				script.script = scriptBody;
				script.planting = plant;
				script.activeOnlyPlantings = activeOnlyPlantings;
				script.reportType = (reportType == null)? ReportType.SCRIPT: reportType;
				script.reportField = reportField;
				logger.warn("reportType == "+ reportType);
				if(startDate != null) script.startDate = startDate;
				if(endDate != null) script.endDate = endDate;
				
				script.save();
			}
			else {
				script = new ReportUserScript(name, description, scriptBody, startDate, endDate, plant, activeOnlyPlantings, reportType, reportField).save();
			}
		}
		else
		{
			script  = new ReportUserScript(name, description, scriptBody, startDate, endDate, plant, activeOnlyPlantings, reportType, reportField).save();
		}
		logger.warn("POST id="+script.id);
		ReportsManagerPublic.viewReports();		
	}
	
	/**
	 * Just deletes
	 * @param id
	 */
	public static void deleteUserScript(Long id) {
		ReportUserScript resp = ReportUserScript.findById(id);
		if(resp != null)
			resp.delete();
		
		ReportsManagerPublic.viewReports();
	}
	
}
