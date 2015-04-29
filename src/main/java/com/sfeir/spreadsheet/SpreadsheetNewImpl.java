/*
package com.sfeir.spreadsheet;


import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SpreadsheetNewImpl implements SpreadsheetNew {

	private static final String URL_SPREADSHEET_SUFFIX = "private/full";
	private static final String DEFAULT_APPLICATION_NAME = "CloudServices";

	@Override
	public Table<Integer, Integer, String> getCells(String key) {
		return getCellsForWorksheetEntry(key, getFirstWorksheetId(key), getSpreadsheetService(DEFAULT_APPLICATION_NAME));
	}

	@Override
	public Table<Integer, Integer, String> getCells(String key, String worksheetTitle) {

		try {
			final SpreadsheetService spreadsheetService = getSpreadsheetService(DEFAULT_APPLICATION_NAME);
			final String worksheetEntryId = getWorksheetEntryId(worksheetTitle, key, spreadsheetService);
		//	LOGGER.info("worksheetEntryId : " + worksheetEntryId);
			return getCellsForWorksheetEntry(key, worksheetEntryId, spreadsheetService);

		} catch (ServiceException | IOException e) {
			e.printStackTrace();
	//		throw new SpreadsheetException(e);
		}
	}

	@Override
	public void insertRow(Map<String, String> columnHeader2cellValues, String key, String applicationName){
		//LOGGER.info("Insert row in first worksheet");

		final SpreadsheetService spreadsheetService = getSpreadsheetService(applicationName);
		insertRowForWorksheetEntry(columnHeader2cellValues, key, getFirstWorksheetId(key), spreadsheetService);
	}

	@Override
	public void insertRow(Map<String, String> columnHeader2cellValues, String key, String worksheetTitle, String applicationName) {
		//LOGGER.info("Insert row in worksheet " + worksheetTitle);

		try {
			final SpreadsheetService spreadsheetService = getSpreadsheetService(applicationName);
			final String worksheetEntryId = getWorksheetEntryId(worksheetTitle, key, spreadsheetService);
			insertRowForWorksheetEntry(columnHeader2cellValues, key, worksheetEntryId, spreadsheetService);

		} catch (ServiceException | IOException e) {
			//throw new SpreadsheetException(e);
		}
	}


	@Override
	public Map<String, String> listWorksheets(String key) throws SpreadsheetException {
		//LOGGER.info("list Worksheets : key = " + key);

		try {

			final URL spreadsheetUrl = buildUrlForWorksheetsFeed(key);
		//	LOGGER.info("getWorksheetEntries : url = " + spreadsheetUrl);

			final SpreadsheetService spreadsheetService = getSpreadsheetService(DEFAULT_APPLICATION_NAME);

			final WorksheetFeed worksheetFeed = spreadsheetService.getFeed(spreadsheetUrl, WorksheetFeed.class);
			for (WorksheetEntry worksheetEntry : worksheetFeed.getEntries()) {
				
			}


			final ImmutableMap.Builder<String, String> worksheetsId2Names = ImmutableMap.builder();
			for (final WorksheetEntry entry : worksheetFeed.getEntries()) {
		//		LOGGER.debug("worksheetsId2NameBuilder" + entry.getId() + " : " + entry.getTitle().getPlainText());
				worksheetsId2Names.put(entry.getId(), entry.getTitle().getPlainText());
			}

			return worksheetsId2Names.build();

		} catch (IOException | ServiceException e) {
			throw new SpreadsheetException(e);
		}
	}

	@Override
	public String findSpreadsheetKeyFromTitle(String spreadsheetTitle) throws SpreadsheetException {
		LOGGER.info("findSpreadsheetKeyFromTitle, spreadsheetTitle = " + spreadsheetTitle);

		if (Strings.isNullOrEmpty(spreadsheetTitle)) {
			throw new SpreadsheetException("SpreadsheetTitle cannot be empty");
		}

		final URL url = buildUrlForAllSpreadsheet();

		final List<SpreadsheetEntry> spreadsheetEntries = getAllSpreadsheets(url);

		String key = "";
		for (final SpreadsheetEntry spreadsheetEntry : spreadsheetEntries) {

			final String currentSpreadsheetTitle = spreadsheetEntry.getTitle().getPlainText();
			if (spreadsheetTitle.equalsIgnoreCase(currentSpreadsheetTitle)) {
				LOGGER.info("Spreadsheet '" + spreadsheetTitle + "' found");

				key = spreadsheetEntry.getKey();
				break;
			}
		}

		if (Strings.isNullOrEmpty(key)) {
			throw new SpreadsheetException("Spreadsheet '" + spreadsheetTitle + "' not found");
		}
		return key;
	}

	protected String getFirstWorksheetId(String key) throws SpreadsheetException {
		final Map<String, String> listWorksheets = listWorksheets(key);
		final String firstWorksheet = Lists.newArrayList(listWorksheets.keySet()).get(0);
		return extractSpreadsheetKeyFromUrl(firstWorksheet);
	}

	private SpreadsheetService getSpreadsheetService(String applicationName) {
		//LOGGER.debug("applicationName = " + applicationName);

		final SpreadsheetService spreadsheetService = new SpreadsheetService(applicationName);
		spreadsheetService.setConnectTimeout(0);

		final OAuthService oAuthService = new OAuthServiceImpl();
		spreadsheetService.setOAuth2Credentials(oAuthService.getCredential(OAuthScope.SPREADSHEETS));

		return spreadsheetService;
	}

	private Table<Integer, Integer, String> getCellsForWorksheetEntry(String key, String worksheetEntryId, SpreadsheetService spreadsheetService) throws SpreadsheetException {
		LOGGER.info("Get cells");

		final URL worksheetCellsUrl = buildUrlFeed(SpreadsheetFeedType.CELLS, worksheetEntryId, key);
		LOGGER.info("worksheetCellsUrl : " + worksheetCellsUrl);
		try {

			final List<CellEntry> cellEntries = spreadsheetService.getFeed(worksheetCellsUrl, CellFeed.class).getEntries();
			LOGGER.info("cellEntries : " + Arrays.toString(cellEntries.toArray()));

			final Table<Integer, Integer, String> cells = HashBasedTable.create();
			for (final CellEntry cellEntry : cellEntries) {
				final Cell cell = cellEntry.getCell();
				cells.put(cell.getRow(), cell.getCol(), cell.getValue());
			}
			return cells;

		} catch (IOException | ServiceException e) {
			throw new SpreadsheetException(e);
		}
	}

	private void insertRowForWorksheetEntry(Map<String, String> columnHeader2cellValues, String key, String worksheetEntryId, SpreadsheetService spreadsheetService) throws SpreadsheetException {
		LOGGER.info("Insert row in worksheet " + worksheetEntryId);

		if (columnHeader2cellValues.isEmpty() || worksheetEntryId.isEmpty()) {
			return;
		}

		final ListEntry row = convertMapCellToListEntry(columnHeader2cellValues);

		try {
			final URL worksheetListUrl = buildUrlFeed(SpreadsheetFeedType.LIST, worksheetEntryId, key);

			final ListEntry insert = spreadsheetService.insert(worksheetListUrl, row);
			LOGGER.debug("insertRowForDefaultWorksheet id : " + insert.getId());

		} catch (IOException | ServiceException e) {
			throw new SpreadsheetException(e);
		}
	}

	private ListEntry convertMapCellToListEntry(Map<String, String> columnHeader2cellValues) {
		final ListEntry row = new ListEntry();
		for (final String key : columnHeader2cellValues.keySet()) {
			row.getCustomElements().setValueLocal(key, columnHeader2cellValues.get(key));
		}
		return row;
	}

	private String getWorksheetEntryId(String worksheetTitle, String key, SpreadsheetService spreadsheetService) throws ServiceException, IOException, SpreadsheetException {
		final String worksheetId = getWorksheetEntryByTitle(worksheetTitle, key, spreadsheetService).getId();
		return extractSpreadsheetKeyFromUrl(worksheetId);
	}

	private WorksheetEntry getWorksheetEntryByTitle(String worksheetTitle, String key, SpreadsheetService spreadsheetService) throws SpreadsheetException, IOException, ServiceException {
		if (Strings.isNullOrEmpty(worksheetTitle)) {
			throw new SpreadsheetException("worksheetTitle cannot be empty");
		}

		final URL spreadsheetUrl = buildUrlForWorksheetsFeed(key);
		final List<WorksheetEntry> worksheetFeeds = spreadsheetService.getFeed(spreadsheetUrl, WorksheetFeed.class).getEntries();

		WorksheetEntry result = null;
		for (final WorksheetEntry worksheetEntry : worksheetFeeds) {
			final String currentWorksheetTitle = worksheetEntry.getTitle().getPlainText();

			if (worksheetTitle.equalsIgnoreCase(currentWorksheetTitle)) {
				LOGGER.debug("Worksheet found");
				result = worksheetEntry;
				break;
			}
		}

		if (result == null) {
			throw new SpreadsheetException("Worksheet not found : " + worksheetTitle);
		}

		return result;
	}

	protected URL buildUrlFeed(SpreadsheetFeedType type, String worksheetId, String key) throws SpreadsheetException {
		try {

			final UrlBuilder builder = new UrlBuilder(OAuthScope.SPREADSHEETS.get())
					.addPath(type.get())
					.addPath(key)
					.addPath(worksheetId)
					.addPath(URL_SPREADSHEET_SUFFIX);
			return builder.toUrl();

		} catch (MalformedURLException e) {
			throw new SpreadsheetException(e);
		}
	}


	protected URL buildUrlForWorksheetsFeed(final String key) throws SpreadsheetException {
		try {
			final UrlBuilder builder = new UrlBuilder(OAuthScope.SPREADSHEETS.get())
					.addPath(SpreadsheetFeedType.WORKSHEETS.get())
					.addPath(key)
					.addPath(URL_SPREADSHEET_SUFFIX);
			return builder.toUrl();

		} catch (MalformedURLException e) {
			throw new SpreadsheetException(e);
		}
	}

	protected URL buildUrlForAllSpreadsheet() throws SpreadsheetException {
		try {

			final UrlBuilder builder = new UrlBuilder(OAuthScope.SPREADSHEETS.get())
					.addPath(SpreadsheetFeedType.SPREADSHEETS.get())
					.addPath(URL_SPREADSHEET_SUFFIX);
			return builder.toUrl();

		} catch (MalformedURLException e) {
			throw new SpreadsheetException(e);
		}
	}

	private List<SpreadsheetEntry> getAllSpreadsheets(URL url) throws SpreadsheetException {
		LOGGER.info("getAllSpreadsheets, url = " + url);
		try {
			final SpreadsheetService spreadsheetService = getSpreadsheetService(DEFAULT_APPLICATION_NAME);
			final SpreadsheetFeed spreadsheetFeed = spreadsheetService.getFeed(url, SpreadsheetFeed.class);
			return spreadsheetFeed.getEntries();

		} catch (IOException | ServiceException e) {
			throw new SpreadsheetException(e);
		}
	}

	protected String extractSpreadsheetKeyFromUrl(String url) {
		return url.substring(url.lastIndexOf("/") + 1);
	}

}
	*/