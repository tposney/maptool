/*
 * This software copyright by various authors including the RPTools.net
 * development team, and licensed under the LGPL Version 3 or, at your option,
 * any later version.
 *
 * Portions of this software were originally covered under the Apache Software
 * License, Version 1.1 or Version 2.0.
 *
 * See the file LICENSE elsewhere in this distribution for license details.
 */

package net.rptools.maptool.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * @author tylere
 */
public class MRUCampaignManager {
	//To increase max mru's need to update mnemonics code
	private static final int DEFAULT_MAX_MRU = 9;
	private final JMenu mruMenu;
	private List<File> mruCampaigns;

	public MRUCampaignManager(JMenu menu) {
		mruMenu = menu;
		mruCampaigns = new ArrayList<File>(DEFAULT_MAX_MRU + 1);
		loadMruCampaignList();
	}

	/**
	 * Returns the MRU Campaigns menu item and sub-menu
	 */
	public JMenu getMRUMenu() {
		return mruMenu;
	}

	/**
	 * Adds a new Campaign to the MRU list, then resort the list and update the menu
	 */
	public void addMRUCampaign(File newCampaign) {
		// FIXME (this coupling is too tight; change the calling function to avoid this call entirely)
		//don't add the autosave recovery file until it is resaved
		if (newCampaign == AutoSaveManager.AUTOSAVE_FILE)
			return;
		// remove and reinsert the campaign so it is at the front of the list
		mruCampaigns.remove(newCampaign);
		mruCampaigns.add(0, newCampaign);
		// remove excess element - we added at most 1 so size is at most DEFAULT_MAX_MRU + 1
		if (mruCampaigns.size() > DEFAULT_MAX_MRU) {
		  mruCampaigns.remove(DEFAULT_MAX_MRU);
		}
		resetMruMenu();
	}

	private void resetMruMenu() {
		mruMenu.removeAll();
		addMRUsToMenu();
		saveMruCampaignList();
	}

	private void addMRUsToMenu() {
		if (mruCampaigns.isEmpty()) {
			mruMenu.add(new JMenuItem("[empty]"));
		} else {
			int i = 1;
			for (ListIterator<File> iter = mruCampaigns.listIterator(); iter.hasNext();) {
				File nextFile = iter.next();
				// Only add existing files.
				if (nextFile.exists()) {
					Action action = new AppActions.OpenMRUCampaign(nextFile, i++);
					mruMenu.add(action);
				}
			}
		}
	}

	private void saveMruCampaignList() {
		AppPreferences.setMruCampaigns(mruCampaigns);
	}

	private void loadMruCampaignList() {
		mruCampaigns = AppPreferences.getMruCampaigns();
		addMRUsToMenu();
	}
}
