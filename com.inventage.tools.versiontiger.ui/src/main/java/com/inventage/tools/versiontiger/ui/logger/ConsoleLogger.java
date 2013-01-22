package com.inventage.tools.versiontiger.ui.logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;

import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.VersioningLoggerItem;

/**
 * This console logger is an implementation of the logger which prints all logger items added to it instantly to the provided {@link BufferedWriter} in an 
 * Apache-like console format.
 * 
 * @author nw
 *
 */
public class ConsoleLogger implements VersioningLogger {

	public static final String CONSOLE_IDENTIFIER = "Version Tiger Console";
	
	public final static String LINE_DELIMITER = "\n";
	public final static String DELIMITER = " - ";
	
	private MessageConsole console;
	private BufferedWriter consoleOutput;
	
	public ConsoleLogger() {
		console = findConsole(CONSOLE_IDENTIFIER);
		consoleOutput = new BufferedWriter(new OutputStreamWriter(console.newOutputStream()));
		
		try {
			console.clearConsole();
			addTitle(consoleOutput);
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to write to console.", e);
		}
	}
	
	@Override
	public VersioningLoggerItem createVersioningLoggerItem() {
		return new ConsoleLoggerItem();
	}

	@Override
	public void addVersioningLoggerItem(VersioningLoggerItem loggerItem) {
		ConsoleLoggerItem item = (ConsoleLoggerItem) loggerItem;
		
		/* We don't log items containing no logger status. */
		if (item.getStatus() == null) {
			return;
		}
		
		try {
			item.writeTo(consoleOutput);
			consoleOutput.append(ConsoleLogger.LINE_DELIMITER);
			consoleOutput.flush();
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to write to console.", e);
		}
	}

	private void addTitle(BufferedWriter buffer) throws IOException {
		buffer.append("Inventage Version Tool Report");
		buffer.append(DELIMITER);
		buffer.append(new SimpleDateFormat().format(new Date()));
		buffer.append(LINE_DELIMITER);
		
		buffer.append("--------------------------------------------------------------------------------");
		buffer.append(LINE_DELIMITER);
	}
	
	
	/**
	 * Retrieves a console of given name or creates a new one. Taken from
	 * http://wiki.eclipse.org/FAQ_How_do_I_write_to_the_console_from_a_plug-in%3F
	 * 
	 * @param name
	 *            the console identifier.
	 * @return a console.
	 */
	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];

		/* If no console was found, a new one is created. */
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	/**
	 * If the console view is hidden, it is put to the front and focus is set on
	 * it.
	 */
	private void putConsoleToForeground() throws PartInitException {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();

		IWorkbenchPage page = win.getActivePage();// obtain the active page
		String id = IConsoleConstants.ID_CONSOLE_VIEW;
		IConsoleView view = (IConsoleView) page.showView(id);
		view.display(console);
	}
	
	/**
	 * Concludes the logging process by refreshing the console and closing the writer.
	 */
	public void close() throws PartInitException, IOException {
		consoleOutput.flush();
		consoleOutput.close();

		/* Set focus on console we wrote to. */
		putConsoleToForeground();
	}
	
}
