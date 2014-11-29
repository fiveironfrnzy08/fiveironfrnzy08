/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ryangray.postd.server;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Simple implementation of a data store using standard Java collections.
 * <p>
 * This class is thread-safe but not persistent (it will lost the data when the
 * app is restarted) - it is meant just as an example.
 */
public final class Datastore {

	private static final List<String> regIds = new ArrayList<String>();
	private static final Logger logger =
			Logger.getLogger(Datastore.class.getName());

	private Datastore() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Registers a device.
	 */
	public static void register(String regId) {
		if (regIds.isEmpty()) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = DriverManager.getConnection("jdbc:mysql://192.168.1.80:3306/news_db","root","ryan135244");
				PreparedStatement statement = con.prepareStatement("SELECT * FROM gcm_devices");
				ResultSet result = statement.executeQuery();
				int i = 0;
				while (result.next()) {
					regIds.add(i, result.getString(i + 1));
					i++;
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.info("Registering " + regId);
		synchronized (regIds) {
			if (!regIds.contains(regId)) {
				regIds.add(regId);
			}
			addToDB(regId);
		}
	}

	private static void addToDB(String regId) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://192.168.1.80:3306/news_db","root","ryan135244");
			PreparedStatement statement = con.prepareStatement("REPLACE INTO gcm_devices (reg_id) VALUES (?)");
			statement.setString(1, regId);
			int result = statement.executeUpdate();
			FileWriter out = new FileWriter("/usr/share/tomcat7/webapps/output_to_database.log", true);
			out.write(result + "\n");
			out.close();
		} catch (ClassNotFoundException e) {
			try {
				FileWriter otherOut = new FileWriter("/usr/share/tomcat7/webapps/output_to_database.log", true);
				otherOut.write(e + "\n");
				otherOut.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (SQLException e) {
			try {
				FileWriter otherOut = new FileWriter("/usr/share/tomcat7/webapps/output_to_database.log", true);
				otherOut.write(e + "\n");
				otherOut.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (IOException e) {
			try {
				FileWriter otherOut = new FileWriter("/usr/share/tomcat7/webapps/output_to_database.log", true);
				otherOut.write(e + "\n");
				otherOut.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	/**
	 * Unregisters a device.
	 */
	public static void unregister(String regId) {
		logger.info("Unregistering " + regId);
		synchronized (regIds) {
			regIds.remove(regId);
		}
	}

	/**
	 * Updates the registration id of a device.
	 */
	public static void updateRegistration(String oldId, String newId) {
		logger.info("Updating " + oldId + " to " + newId);
		synchronized (regIds) {
			regIds.remove(oldId);
			regIds.add(newId);
		}
	}

	/**
	 * Gets all registered devices.
	 */
	public static List<String> getDevices() {
		synchronized (regIds) {
			return new ArrayList<String>(regIds);
		}
	}

}
