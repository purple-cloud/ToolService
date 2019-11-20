-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: 19. Nov, 2019 13:32 PM
-- Tjener-versjon: 10.4.6-MariaDB
-- PHP Version: 7.3.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `testing123`
--

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `borrows`
--

CREATE TABLE `borrows` (
  `borrow_id` int(32) NOT NULL,
  `employee_id` int(32) NOT NULL,
  `tool_id` int(32) NOT NULL,
  `expiration_date` date NOT NULL,
  `date_created` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `employees`
--

CREATE TABLE `employees` (
  `employee_id` int(32) NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` int(32) NOT NULL,
  `date_created` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `employee_project_leader`
--

CREATE TABLE `employee_project_leader` (
  `employee_id` int(32) NOT NULL,
  `leader_id` int(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `employee_roles`
--

CREATE TABLE `employee_roles` (
  `employee_id` int(32) NOT NULL,
  `role_id` int(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `materials`
--

CREATE TABLE `materials` (
  `material_id` int(32) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `desc` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `date_created` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `permissions`
--

CREATE TABLE `permissions` (
  `permission_id` int(32) NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `projects`
--

CREATE TABLE `projects` (
  `project_id` int(32) NOT NULL,
  `name` varchar(255) NOT NULL,
  `location` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `project_employees`
--

CREATE TABLE `project_employees` (
  `employee_id` int(32) NOT NULL,
  `project_id` int(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `project_leader`
--

CREATE TABLE `project_leader` (
  `leader_id` int(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `project_leader_materials`
--

CREATE TABLE `project_leader_materials` (
  `material_id` int(32) NOT NULL,
  `leader_id` int(32) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `project_leader_project_role`
--

CREATE TABLE `project_leader_project_role` (
  `leader_id` int(32) NOT NULL,
  `project_role_id` int(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `project_permissions`
--

CREATE TABLE `project_permissions` (
  `project_permission_id` int(32) NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `project_project_leader`
--

CREATE TABLE `project_project_leader` (
  `project_id` int(32) NOT NULL,
  `leader_id` int(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `project_roles`
--

CREATE TABLE `project_roles` (
  `project_role_id` int(32) NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `project_tools`
--

CREATE TABLE `project_tools` (
  `tool_id` int(32) NOT NULL,
  `project_id` int(32) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `pr_prp`
--

CREATE TABLE `pr_prp` (
  `project_role_id` int(32) NOT NULL,
  `project_permission_id` int(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `roles`
--

CREATE TABLE `roles` (
  `role_id` int(32) NOT NULL,
  `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `role_permissions`
--

CREATE TABLE `role_permissions` (
  `role_id` int(32) NOT NULL,
  `permission_id` int(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `tools`
--

CREATE TABLE `tools` (
  `tool_id` int(32) NOT NULL,
  `name` varchar(255) NOT NULL,
  `desc` varchar(255) DEFAULT NULL,
  `location` varchar(255) NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `date_created` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `tool_project_leader`
--

CREATE TABLE `tool_project_leader` (
  `tool_id` int(32) NOT NULL,
  `leader_id` int(32) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `borrows`
--
ALTER TABLE `borrows`
  ADD PRIMARY KEY (`borrow_id`),
  ADD KEY `fk_borrows_employees_1` (`employee_id`),
  ADD KEY `fk_borrows_tools_1` (`tool_id`);

--
-- Indexes for table `employees`
--
ALTER TABLE `employees`
  ADD PRIMARY KEY (`employee_id`);

--
-- Indexes for table `employee_project_leader`
--
ALTER TABLE `employee_project_leader`
  ADD PRIMARY KEY (`employee_id`,`leader_id`),
  ADD KEY `fk_employee_project_leader_project_leader_1` (`leader_id`);

--
-- Indexes for table `employee_roles`
--
ALTER TABLE `employee_roles`
  ADD PRIMARY KEY (`employee_id`,`role_id`),
  ADD KEY `fk_roles_employees` (`role_id`);

--
-- Indexes for table `materials`
--
ALTER TABLE `materials`
  ADD PRIMARY KEY (`material_id`);

--
-- Indexes for table `permissions`
--
ALTER TABLE `permissions`
  ADD PRIMARY KEY (`permission_id`);

--
-- Indexes for table `projects`
--
ALTER TABLE `projects`
  ADD PRIMARY KEY (`project_id`);

--
-- Indexes for table `project_employees`
--
ALTER TABLE `project_employees`
  ADD PRIMARY KEY (`employee_id`,`project_id`),
  ADD KEY `fk_project_employees1_project_1` (`project_id`);

--
-- Indexes for table `project_leader`
--
ALTER TABLE `project_leader`
  ADD PRIMARY KEY (`leader_id`);

--
-- Indexes for table `project_leader_materials`
--
ALTER TABLE `project_leader_materials`
  ADD PRIMARY KEY (`material_id`),
  ADD KEY `fk_project_leader_materials_project_leader_1` (`leader_id`);

--
-- Indexes for table `project_leader_project_role`
--
ALTER TABLE `project_leader_project_role`
  ADD PRIMARY KEY (`leader_id`,`project_role_id`),
  ADD KEY `fk_project_leader_project_role_project_roles_1` (`project_role_id`);

--
-- Indexes for table `project_permissions`
--
ALTER TABLE `project_permissions`
  ADD PRIMARY KEY (`project_permission_id`);

--
-- Indexes for table `project_project_leader`
--
ALTER TABLE `project_project_leader`
  ADD PRIMARY KEY (`project_id`,`leader_id`),
  ADD KEY `fk_project_project_leader_project_leader_1` (`leader_id`);

--
-- Indexes for table `project_roles`
--
ALTER TABLE `project_roles`
  ADD PRIMARY KEY (`project_role_id`);

--
-- Indexes for table `project_tools`
--
ALTER TABLE `project_tools`
  ADD PRIMARY KEY (`tool_id`),
  ADD KEY `fk_project_tools_project_1` (`project_id`);

--
-- Indexes for table `pr_prp`
--
ALTER TABLE `pr_prp`
  ADD PRIMARY KEY (`project_role_id`,`project_permission_id`),
  ADD KEY `fk_pr_prp_project_permissions_1` (`project_permission_id`);

--
-- Indexes for table `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`role_id`);

--
-- Indexes for table `role_permissions`
--
ALTER TABLE `role_permissions`
  ADD PRIMARY KEY (`role_id`,`permission_id`),
  ADD KEY `fk_permissions_role` (`permission_id`);

--
-- Indexes for table `tools`
--
ALTER TABLE `tools`
  ADD PRIMARY KEY (`tool_id`);

--
-- Indexes for table `tool_project_leader`
--
ALTER TABLE `tool_project_leader`
  ADD PRIMARY KEY (`tool_id`),
  ADD KEY `fk_tool_project_leader_project_leader_1` (`leader_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `borrows`
--
ALTER TABLE `borrows`
  MODIFY `borrow_id` int(32) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `employees`
--
ALTER TABLE `employees`
  MODIFY `employee_id` int(32) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `materials`
--
ALTER TABLE `materials`
  MODIFY `material_id` int(32) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `permissions`
--
ALTER TABLE `permissions`
  MODIFY `permission_id` int(32) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `projects`
--
ALTER TABLE `projects`
  MODIFY `project_id` int(32) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `project_leader`
--
ALTER TABLE `project_leader`
  MODIFY `leader_id` int(32) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `project_permissions`
--
ALTER TABLE `project_permissions`
  MODIFY `project_permission_id` int(32) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `project_roles`
--
ALTER TABLE `project_roles`
  MODIFY `project_role_id` int(32) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `roles`
--
ALTER TABLE `roles`
  MODIFY `role_id` int(32) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tools`
--
ALTER TABLE `tools`
  MODIFY `tool_id` int(32) NOT NULL AUTO_INCREMENT;

--
-- Begrensninger for dumpede tabeller
--

--
-- Begrensninger for tabell `borrows`
--
ALTER TABLE `borrows`
  ADD CONSTRAINT `fk_borrows_employees_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_borrows_tools_1` FOREIGN KEY (`tool_id`) REFERENCES `tools` (`tool_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Begrensninger for tabell `employee_project_leader`
--
ALTER TABLE `employee_project_leader`
  ADD CONSTRAINT `fk_employee_project_leader_employees_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_employee_project_leader_project_leader_1` FOREIGN KEY (`leader_id`) REFERENCES `project_leader` (`leader_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Begrensninger for tabell `employee_roles`
--
ALTER TABLE `employee_roles`
  ADD CONSTRAINT `fk_employees_roles` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_roles_employees` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Begrensninger for tabell `project_employees`
--
ALTER TABLE `project_employees`
  ADD CONSTRAINT `fk_project_employees1_employees_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_project_employees1_project_1` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Begrensninger for tabell `project_leader_materials`
--
ALTER TABLE `project_leader_materials`
  ADD CONSTRAINT `fk_project_leader_materials_materials_1` FOREIGN KEY (`material_id`) REFERENCES `materials` (`material_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_project_leader_materials_project_leader_1` FOREIGN KEY (`leader_id`) REFERENCES `project_leader` (`leader_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Begrensninger for tabell `project_leader_project_role`
--
ALTER TABLE `project_leader_project_role`
  ADD CONSTRAINT `fk_project_leader_project_role_project_leader_1` FOREIGN KEY (`leader_id`) REFERENCES `project_leader` (`leader_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_project_leader_project_role_project_roles_1` FOREIGN KEY (`project_role_id`) REFERENCES `project_roles` (`project_role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Begrensninger for tabell `project_project_leader`
--
ALTER TABLE `project_project_leader`
  ADD CONSTRAINT `fk_project_project_leader_project_1` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_project_project_leader_project_leader_1` FOREIGN KEY (`leader_id`) REFERENCES `project_leader` (`leader_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Begrensninger for tabell `project_tools`
--
ALTER TABLE `project_tools`
  ADD CONSTRAINT `fk_project_tools_project_1` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_project_tools_tools_1` FOREIGN KEY (`tool_id`) REFERENCES `tools` (`tool_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Begrensninger for tabell `pr_prp`
--
ALTER TABLE `pr_prp`
  ADD CONSTRAINT `fk_pr_prp_project_permissions_1` FOREIGN KEY (`project_permission_id`) REFERENCES `project_permissions` (`project_permission_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_pr_prp_project_roles_1` FOREIGN KEY (`project_role_id`) REFERENCES `project_roles` (`project_role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Begrensninger for tabell `role_permissions`
--
ALTER TABLE `role_permissions`
  ADD CONSTRAINT `fk_permissions_role` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`permission_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_roles_permissions` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Begrensninger for tabell `tool_project_leader`
--
ALTER TABLE `tool_project_leader`
  ADD CONSTRAINT `fk_tool_project_leader_project_leader_1` FOREIGN KEY (`leader_id`) REFERENCES `project_leader` (`leader_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_tool_project_leader_tools_1` FOREIGN KEY (`tool_id`) REFERENCES `tools` (`tool_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
