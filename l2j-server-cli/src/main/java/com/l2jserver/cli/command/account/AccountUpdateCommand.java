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
package com.l2jserver.cli.command.account;

import com.l2jserver.cli.command.AbstractCommand;
import com.l2jserver.cli.dao.AccountDAO;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Account update command.
 * @author Zoey76
 */
@Command(name = "update")
public class AccountUpdateCommand extends AbstractCommand {
	
	@Option(names = {
		"-u",
		"--username"
	}, required = true, description = "Username")
	private String username;
	
	@Option(names = {
		"-p",
		"--password"
	}, interactive = true, description = "Password")
	private String password;
	
	@Option(names = {
		"-a",
		"--access-level"
	}, description = "Access Level")
	private Integer accessLevel;
	
	@Override
	public void run() {
		System.out.println("Updating account " + username + " (" + accessLevel + ")");
		
		if (password != null && accessLevel != null) {
			final var createdUpdated = AccountDAO.getInstance().upsert(username, password, accessLevel);
			if (createdUpdated) {
				System.out.println("Account " + username + " has been created or updated.");
			}
			return;
		}
		
		if (accessLevel != null) {
			final var updated = AccountDAO.getInstance().changeAccountLevel(username, accessLevel);
			if (updated) {
				System.out.println("Account " + username + " has been updated.");
			} else {
				System.out.println("Account " + username + " does not exist!");
			}
		}
	}
}
