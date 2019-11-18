CREATE TABLE "borrows" (
"borrow_id" int4 NOT NULL,
"employee_id" int4 NOT NULL,
"tool_id" int4 NOT NULL,
"expiration_date" date NOT NULL,
CONSTRAINT "borrows_pkey" PRIMARY KEY ("borrow_id") 
)
WITHOUT OIDS;
ALTER TABLE "borrows" OWNER TO "postgres";

CREATE TABLE "employee_project_leader" (
"employee_id" int4 NOT NULL,
"leader_id" int4 NOT NULL,
CONSTRAINT "employee_project_leader_pkey" PRIMARY KEY ("employee_id", "leader_id") 
)
WITHOUT OIDS;
ALTER TABLE "employee_project_leader" OWNER TO "postgres";

CREATE TABLE "employee_roles" (
"employee_id" int4 NOT NULL,
"role_id" int4 NOT NULL,
CONSTRAINT "employee_roles_pkey" PRIMARY KEY ("employee_id", "role_id") 
)
WITHOUT OIDS;
ALTER TABLE "employee_roles" OWNER TO "postgres";

CREATE TABLE "employees" (
"employee_id" int4 NOT NULL,
"name" varchar(255) COLLATE "default" NOT NULL,
"email" varchar(255) COLLATE "default" NOT NULL,
"username" varchar(255) COLLATE "default" NOT NULL,
"password" varchar(255) COLLATE "default" NOT NULL,
"phone" int4 NOT NULL,
CONSTRAINT "employees_pkey" PRIMARY KEY ("employee_id") 
)
WITHOUT OIDS;
ALTER TABLE "employees" OWNER TO "postgres";

CREATE TABLE "materials" (
"material_id" int4 NOT NULL,
"name" varchar(255) COLLATE "default",
"desc" varchar(255) COLLATE "default",
"location" varchar(255) COLLATE "default",
"image" varchar(255) COLLATE "default",
"date_created" date,
CONSTRAINT "materials_pkey" PRIMARY KEY ("material_id") 
)
WITHOUT OIDS;
ALTER TABLE "materials" OWNER TO "postgres";

CREATE TABLE "permissions" (
"permission_id" int4 NOT NULL,
"name" varchar(255) COLLATE "default" NOT NULL,
CONSTRAINT "permissions_pkey" PRIMARY KEY ("permission_id") 
)
WITHOUT OIDS;
ALTER TABLE "permissions" OWNER TO "postgres";

CREATE TABLE "pr_prp" (
"project_role_id" int4 NOT NULL,
"project_permission_id" int4 NOT NULL,
CONSTRAINT "pr_prp_pkey" PRIMARY KEY ("project_role_id", "project_permission_id") 
)
WITHOUT OIDS;
ALTER TABLE "pr_prp" OWNER TO "postgres";

CREATE TABLE "project_employees" (
"employee_id" int4 NOT NULL,
"project_id" int4 NOT NULL,
CONSTRAINT "project_employees_pkey" PRIMARY KEY ("employee_id", "project_id") 
)
WITHOUT OIDS;
ALTER TABLE "project_employees" OWNER TO "postgres";

CREATE TABLE "project_leader" (
"leader_id" int4 NOT NULL,
CONSTRAINT "project_leader_pkey" PRIMARY KEY ("leader_id") 
)
WITHOUT OIDS;
ALTER TABLE "project_leader" OWNER TO "postgres";

CREATE TABLE "project_leader_materials" (
"material_id" int4 NOT NULL,
"leader_id" int4,
CONSTRAINT "project_leader_materials_pkey" PRIMARY KEY ("material_id") 
)
WITHOUT OIDS;
ALTER TABLE "project_leader_materials" OWNER TO "postgres";

CREATE TABLE "project_leader_project_role" (
"leader_id" int4 NOT NULL,
"project_role_id" int4 NOT NULL,
CONSTRAINT "project_leader_project_role_pkey" PRIMARY KEY ("leader_id", "project_role_id") 
)
WITHOUT OIDS;
ALTER TABLE "project_leader_project_role" OWNER TO "postgres";

CREATE TABLE "project_permissions" (
"project_permission_id" int4 NOT NULL,
"name" varchar(255) COLLATE "default" NOT NULL,
CONSTRAINT "project_permissions_pkey" PRIMARY KEY ("project_permission_id") 
)
WITHOUT OIDS;
ALTER TABLE "project_permissions" OWNER TO "postgres";

CREATE TABLE "project_project_leader" (
"project_id" int4 NOT NULL,
"leader_id" int4 NOT NULL,
CONSTRAINT "project_project_leader_pkey" PRIMARY KEY ("project_id", "leader_id") 
)
WITHOUT OIDS;
ALTER TABLE "project_project_leader" OWNER TO "postgres";

CREATE TABLE "project_roles" (
"project_role_id" int4 NOT NULL,
"name" varchar(255) COLLATE "default" NOT NULL,
CONSTRAINT "project_roles_pkey" PRIMARY KEY ("project_role_id") 
)
WITHOUT OIDS;
ALTER TABLE "project_roles" OWNER TO "postgres";

CREATE TABLE "project_tools" (
"tool_id" int4 NOT NULL,
"project_id" int4,
CONSTRAINT "project_tools_pkey" PRIMARY KEY ("tool_id") 
)
WITHOUT OIDS;
ALTER TABLE "project_tools" OWNER TO "postgres";

CREATE TABLE "projects" (
"project_id" int4 NOT NULL,
"name" varchar(255) COLLATE "default" NOT NULL,
"location" varchar(255) COLLATE "default" NOT NULL,
CONSTRAINT "projects_pkey" PRIMARY KEY ("project_id") 
)
WITHOUT OIDS;
ALTER TABLE "projects" OWNER TO "postgres";

CREATE TABLE "role_permissions" (
"role_id" int4 NOT NULL,
"permission_id" int4 NOT NULL,
CONSTRAINT "role_permissions_pkey" PRIMARY KEY ("role_id", "permission_id") 
)
WITHOUT OIDS;
ALTER TABLE "role_permissions" OWNER TO "postgres";

CREATE TABLE "roles" (
"role_id" int4 NOT NULL,
"name" varchar(255) COLLATE "default",
CONSTRAINT "roles_pkey" PRIMARY KEY ("role_id") 
)
WITHOUT OIDS;
ALTER TABLE "roles" OWNER TO "postgres";

CREATE TABLE "tool_project_leader" (
"tool_id" int4 NOT NULL,
"leader_id" int4,
CONSTRAINT "tool_project_leader_pkey" PRIMARY KEY ("tool_id") 
)
WITHOUT OIDS;
ALTER TABLE "tool_project_leader" OWNER TO "postgres";

CREATE TABLE "tools" (
"tool_id" int4 NOT NULL,
"name" varchar(255) COLLATE "default" NOT NULL,
"desc" varchar(255),
"location" varchar(255) NOT NULL,
"image" varchar(255),
"date_created" date NOT NULL,
CONSTRAINT "tools_pkey" PRIMARY KEY ("tool_id") 
)
WITHOUT OIDS;
ALTER TABLE "tools" OWNER TO "postgres";


ALTER TABLE "borrows" ADD CONSTRAINT "fk_borrows_employees_1" FOREIGN KEY ("employee_id") REFERENCES "employees" ("employee_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "borrows" ADD CONSTRAINT "fk_borrows_tools_1" FOREIGN KEY ("tool_id") REFERENCES "tools" ("tool_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "employee_project_leader" ADD CONSTRAINT "fk_employee_project_leader_employees_1" FOREIGN KEY ("employee_id") REFERENCES "employees" ("employee_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "employee_project_leader" ADD CONSTRAINT "fk_employee_project_leader_project_leader_1" FOREIGN KEY ("leader_id") REFERENCES "project_leader" ("leader_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "employee_roles" ADD CONSTRAINT "fk_employees_roles" FOREIGN KEY ("employee_id") REFERENCES "employees" ("employee_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "employee_roles" ADD CONSTRAINT "fk_roles_employees" FOREIGN KEY ("role_id") REFERENCES "roles" ("role_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "pr_prp" ADD CONSTRAINT "fk_pr_prp_project_permissions_1" FOREIGN KEY ("project_permission_id") REFERENCES "project_permissions" ("project_permission_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "pr_prp" ADD CONSTRAINT "fk_pr_prp_project_roles_1" FOREIGN KEY ("project_role_id") REFERENCES "project_roles" ("project_role_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "project_employees" ADD CONSTRAINT "fk_project_employees1_employees_1" FOREIGN KEY ("employee_id") REFERENCES "employees" ("employee_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "project_employees" ADD CONSTRAINT "fk_project_employees1_project_1" FOREIGN KEY ("project_id") REFERENCES "projects" ("project_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "project_leader_materials" ADD CONSTRAINT "fk_project_leader_materials_materials_1" FOREIGN KEY ("material_id") REFERENCES "materials" ("material_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "project_leader_materials" ADD CONSTRAINT "fk_project_leader_materials_project_leader_1" FOREIGN KEY ("leader_id") REFERENCES "project_leader" ("leader_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "project_leader_project_role" ADD CONSTRAINT "fk_project_leader_project_role_project_leader_1" FOREIGN KEY ("leader_id") REFERENCES "project_leader" ("leader_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "project_leader_project_role" ADD CONSTRAINT "fk_project_leader_project_role_project_roles_1" FOREIGN KEY ("project_role_id") REFERENCES "project_roles" ("project_role_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "project_project_leader" ADD CONSTRAINT "fk_project_project_leader_project_1" FOREIGN KEY ("project_id") REFERENCES "projects" ("project_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "project_project_leader" ADD CONSTRAINT "fk_project_project_leader_project_leader_1" FOREIGN KEY ("leader_id") REFERENCES "project_leader" ("leader_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "project_tools" ADD CONSTRAINT "fk_project_tools_project_1" FOREIGN KEY ("project_id") REFERENCES "projects" ("project_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "project_tools" ADD CONSTRAINT "fk_project_tools_tools_1" FOREIGN KEY ("tool_id") REFERENCES "tools" ("tool_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "role_permissions" ADD CONSTRAINT "fk_permissions_role" FOREIGN KEY ("permission_id") REFERENCES "permissions" ("permission_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "role_permissions" ADD CONSTRAINT "fk_roles_permissions" FOREIGN KEY ("role_id") REFERENCES "roles" ("role_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "tool_project_leader" ADD CONSTRAINT "fk_tool_project_leader_project_leader_1" FOREIGN KEY ("leader_id") REFERENCES "project_leader" ("leader_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "tool_project_leader" ADD CONSTRAINT "fk_tool_project_leader_tools_1" FOREIGN KEY ("tool_id") REFERENCES "tools" ("tool_id") ON DELETE NO ACTION ON UPDATE NO ACTION;

