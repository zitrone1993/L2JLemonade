/*
 * Copyright © 2019 L2J Server
 *
 * This file is part of L2J Server.
 *
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.cli.command;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.cli.model.CloneType;
import com.l2jserver.cli.util.LoggerProgressMonitor;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Code command.
 * @author Zoey76
 */
@Command(name = "code", aliases = "c")
public class CodeCommand extends AbstractCommand {
	
	private static final Logger LOG = LoggerFactory.getLogger(CodeCommand.class);
	
	private static final LoggerProgressMonitor LOGGER_PROGRESS_MONITOR = new LoggerProgressMonitor(LOG);
	
	@Option(names = "--core-repository", defaultValue = "https://bitbucket.org/l2jserver/l2j_server.git", description = "Core repository")
	private String coreRepository = "https://bitbucket.org/l2jserver/l2j_server.git";
	
	@Option(names = "--datapack-repository", defaultValue = "https://bitbucket.org/l2jserver/l2j_server.git", description = "Core repository")
	private String datapackRepository = "https://bitbucket.org/l2jserver/l2j_datapack.git";
	
	@Option(names = "--core-directory", defaultValue = "/l2j/git/l2j_server", description = "Core directory")
	private File coreDirectory = new File("/l2j/git/l2j_server");
	
	@Option(names = "--datapack-directory", defaultValue = "/l2j/git/l2j_datapack", description = "DataPack directory")
	private File datapackDirectory = new File("/l2j/git/l2j_datapack");
	
	@Option(names = "--clone", defaultValue = "BOTH", description = "Clone BOTH|CORE|DATAPACK")
	private CloneType cloneType = CloneType.BOTH;
	
	@Override
	public void run() {
		try {
			switch (cloneType) {
				case BOTH: {
					LOG.info("Clonning L2J Server");
					cloneRepository(coreRepository, coreDirectory);
					
					LOG.info("Clonning L2J DataPack");
					cloneRepository(datapackRepository, datapackDirectory);
					break;
				}
				case CORE: {
					LOG.info("Clonning L2J Server");
					cloneRepository(coreRepository, coreDirectory);
					break;
				}
				case DATAPACK: {
					LOG.info("Clonning L2J DataPack");
					cloneRepository(datapackRepository, datapackDirectory);
					break;
				}
			}
		} catch (Exception ex) {
			LOG.error("Unable to get the code!", ex);
		}
	}
	
	private void cloneRepository(String repository, File directory) {
		try {
			Git.cloneRepository() //
				.setURI(repository) //
				.setDirectory(directory) //
				.setProgressMonitor(LOGGER_PROGRESS_MONITOR) //
				.call();
		} catch (Exception ex) {
			LOG.error("Unable to get the code!", ex);
		}
	}
}
