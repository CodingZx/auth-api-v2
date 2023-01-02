
-- ----------------------------
-- Table structure for a_account
-- ----------------------------
DROP TABLE IF EXISTS "public"."a_account";
CREATE TABLE "public"."a_account" (
                                      "id" uuid NOT NULL,
                                      "user_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                      "real_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                      "password" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                      "create_time" timestamp(6) NOT NULL,
                                      "role_id" uuid NOT NULL,
                                      "status" bool NOT NULL DEFAULT true
)
;
COMMENT ON COLUMN "public"."a_account"."user_name" IS '登录账号';
COMMENT ON COLUMN "public"."a_account"."real_name" IS '真实姓名';
COMMENT ON COLUMN "public"."a_account"."password" IS '密码';
COMMENT ON COLUMN "public"."a_account"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."a_account"."role_id" IS '角色ID';
COMMENT ON COLUMN "public"."a_account"."status" IS '状态 true:正常';

-- ----------------------------
-- Records of a_account
-- ----------------------------
INSERT INTO "public"."a_account" VALUES ('00000000-0000-0000-0000-000000000000', 'admin', '管理员', 'BDwiczqLYra2CT6TAnCxfbr8klxQBTB7wBAKDOscUi7fuSH0bURJy9+W8UT1V751JWVEMyhraE1YT+GqKJv3XgtyDKZVnnoUTZBKr2GTi7vhdCwtXKOXAUKOdxMjp1KyfIjiweX/hZijBLS4peUhbMGkaAdqHLhyrbiDBkxWB8Pe79zOCQ==', '2022-05-14 14:10:14.27503', '00000000-0000-0000-0000-000000000000', 't');

-- ----------------------------
-- Table structure for a_config
-- ----------------------------
DROP TABLE IF EXISTS "public"."a_config";
CREATE TABLE "public"."a_config" (
                                     "id" uuid NOT NULL,
                                     "config_key" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
                                     "values" text COLLATE "pg_catalog"."default" NOT NULL,
                                     "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."a_config"."config_key" IS '配置Key';
COMMENT ON COLUMN "public"."a_config"."values" IS '字典的值';
COMMENT ON COLUMN "public"."a_config"."create_time" IS '创建时间';

-- ----------------------------
-- Records of a_config
-- ----------------------------

-- ----------------------------
-- Table structure for a_exception_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."a_exception_log";
CREATE TABLE "public"."a_exception_log" (
                                            "id" uuid NOT NULL,
                                            "log_msg" text COLLATE "pg_catalog"."default" NOT NULL,
                                            "msg_detail" text COLLATE "pg_catalog"."default" NOT NULL,
                                            "msg_md5" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                            "status" int4 NOT NULL,
                                            "counter" int8 NOT NULL,
                                            "create_time" timestamp(6) NOT NULL,
                                            "last_report_time" timestamp(6) NOT NULL
)
;
COMMENT ON COLUMN "public"."a_exception_log"."log_msg" IS '错误信息';
COMMENT ON COLUMN "public"."a_exception_log"."msg_detail" IS '错误日志详情';
COMMENT ON COLUMN "public"."a_exception_log"."msg_md5" IS '错误信息摘要 唯一';
COMMENT ON COLUMN "public"."a_exception_log"."status" IS '状态';
COMMENT ON COLUMN "public"."a_exception_log"."counter" IS '上报次数';
COMMENT ON COLUMN "public"."a_exception_log"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."a_exception_log"."last_report_time" IS '最后上报时间';

-- ----------------------------
-- Records of a_exception_log
-- ----------------------------

-- ----------------------------
-- Table structure for a_exception_log_backup
-- ----------------------------
DROP TABLE IF EXISTS "public"."a_exception_log_backup";
CREATE TABLE "public"."a_exception_log_backup" (
                                                   "id" uuid NOT NULL,
                                                   "log_msg" text COLLATE "pg_catalog"."default" NOT NULL,
                                                   "msg_detail" text COLLATE "pg_catalog"."default" NOT NULL,
                                                   "msg_md5" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                                   "create_time" timestamp(6) NOT NULL
)
;
COMMENT ON COLUMN "public"."a_exception_log_backup"."log_msg" IS '错误信息';
COMMENT ON COLUMN "public"."a_exception_log_backup"."msg_detail" IS '错误信息详情';
COMMENT ON COLUMN "public"."a_exception_log_backup"."msg_md5" IS '错误信息摘要';
COMMENT ON COLUMN "public"."a_exception_log_backup"."create_time" IS '创建时间';

-- ----------------------------
-- Records of a_exception_log_backup
-- ----------------------------

-- ----------------------------
-- Table structure for a_ip_limit
-- ----------------------------
DROP TABLE IF EXISTS "public"."a_ip_limit";
CREATE TABLE "public"."a_ip_limit" (
                                       "id" uuid NOT NULL,
                                       "ip_addr" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                       "request_account" text COLLATE "pg_catalog"."default" NOT NULL,
                                       "create_time" timestamp(6) NOT NULL
)
;
COMMENT ON COLUMN "public"."a_ip_limit"."ip_addr" IS 'IP地址';
COMMENT ON COLUMN "public"."a_ip_limit"."request_account" IS '尝试的账号密码';
COMMENT ON COLUMN "public"."a_ip_limit"."create_time" IS '创建时间';

-- ----------------------------
-- Records of a_ip_limit
-- ----------------------------

-- ----------------------------
-- Table structure for a_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."a_log";
CREATE TABLE "public"."a_log" (
                                  "id" uuid NOT NULL,
                                  "title" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
                                  "business_type" int4 NOT NULL,
                                  "method" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                  "request_method" varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
                                  "oper_uri" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                  "oper_ip" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
                                  "oper_param" text COLLATE "pg_catalog"."default" NOT NULL,
                                  "error_msg" text COLLATE "pg_catalog"."default" NOT NULL,
                                  "oper_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
                                  "create_time" timestamp(6) NOT NULL
)
;
COMMENT ON COLUMN "public"."a_log"."title" IS '标题';
COMMENT ON COLUMN "public"."a_log"."business_type" IS '操作类型';
COMMENT ON COLUMN "public"."a_log"."method" IS '请求方法地址';
COMMENT ON COLUMN "public"."a_log"."request_method" IS '请求方式';
COMMENT ON COLUMN "public"."a_log"."oper_uri" IS '请求uri';
COMMENT ON COLUMN "public"."a_log"."oper_ip" IS '请求IP';
COMMENT ON COLUMN "public"."a_log"."oper_param" IS '请求参数';
COMMENT ON COLUMN "public"."a_log"."error_msg" IS '错误信息';
COMMENT ON COLUMN "public"."a_log"."oper_name" IS '操作人';
COMMENT ON COLUMN "public"."a_log"."create_time" IS '操作时间';

-- ----------------------------
-- Records of a_log
-- ----------------------------
INSERT INTO "public"."a_log" VALUES ('b9287ef2-6fc6-11ed-b9ab-e538a8f30c20', '清空日志', 3, 'fml.plus.auth.controller.LogController.clear()', 'DELETE', '/log/clear', '127.0.0.1', '{}', '', '管理员', '2022-11-29 17:18:01.936698');
INSERT INTO "public"."a_log" VALUES ('b9488a13-6fc6-11ed-b9ab-f7bbf95a3634', '日志列表', 2, 'fml.plus.auth.controller.LogController.list()', 'GET', '/log/list', '127.0.0.1', '{"size":["20"],"operName":[""],"start":[""],"end":[""],"page":["1"]}', '', '管理员', '2022-11-29 17:18:02.14409');
INSERT INTO "public"."a_log" VALUES ('babe3a24-6fc6-11ed-b9ab-472c50f93518', '查询菜单', 2, 'fml.plus.auth.controller.MenuController.list()', 'GET', '/menu/list', '127.0.0.1', '{}', '', '管理员', '2022-11-29 17:18:04.593704');
INSERT INTO "public"."a_log" VALUES ('bb4ae9c5-6fc6-11ed-b9ab-517d651477c6', '角色列表', 2, 'fml.plus.auth.controller.RoleController.list()', 'GET', '/role/list', '127.0.0.1', '{"size":["20"],"roleName":[""],"page":["1"]}', '', '管理员', '2022-11-29 17:18:05.514247');

-- ----------------------------
-- Table structure for a_menu
-- ----------------------------
DROP TABLE IF EXISTS "public"."a_menu";
CREATE TABLE "public"."a_menu" (
                                   "id" uuid NOT NULL,
                                   "menu_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                   "icon" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                   "sort_by" int4 NOT NULL,
                                   "create_time" timestamp(0) NOT NULL,
                                   "menu_type" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                   "parent_id" uuid,
                                   "permission" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
                                   "menu_path" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying
)
;
COMMENT ON COLUMN "public"."a_menu"."menu_name" IS '菜单名称';
COMMENT ON COLUMN "public"."a_menu"."icon" IS '菜单图标';
COMMENT ON COLUMN "public"."a_menu"."sort_by" IS '排序值';
COMMENT ON COLUMN "public"."a_menu"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."a_menu"."menu_type" IS '菜单类型';
COMMENT ON COLUMN "public"."a_menu"."parent_id" IS '父级菜单ID';
COMMENT ON COLUMN "public"."a_menu"."permission" IS '对应权限';
COMMENT ON COLUMN "public"."a_menu"."menu_path" IS '菜单对应URL';

-- ----------------------------
-- Records of a_menu
-- ----------------------------
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-000000000003', '角色列表', '', 2, '2022-05-14 14:10:14', 'menu', '00000000-0000-0000-0000-000000000001', 'auth:role', '/auth/role/list');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-000000000000', '菜单列表', '', 3, '2022-05-14 14:10:14', 'menu', '00000000-0000-0000-0000-000000000001', 'auth:menu', '/auth/menu/list');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-000000000008', '新增', '', 1, '2022-05-14 14:10:14', 'button', '00000000-0000-0000-0000-000000000003', 'auth:role:add', '');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-000000000009', '编辑', '', 2, '2022-05-14 14:10:14', 'button', '00000000-0000-0000-0000-000000000003', 'auth:role:edit', '');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-00000000000a', '删除', '', 2, '2022-05-14 14:10:14', 'button', '00000000-0000-0000-0000-000000000003', 'auth:role:delete', '');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-00000000000b', '新增', '', 1, '2022-05-14 14:10:14', 'button', '00000000-0000-0000-0000-000000000000', 'auth:menu:add', '');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-00000000000c', '编辑', '', 2, '2022-05-14 14:10:14', 'button', '00000000-0000-0000-0000-000000000000', 'auth:menu:edit', '');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-00000000000d', '删除', '', 3, '2022-05-14 14:10:14', 'button', '00000000-0000-0000-0000-000000000000', 'auth:menu:delete', '');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-00000000000e', '日志列表', '', 4, '2022-05-14 14:10:14', 'menu', '00000000-0000-0000-0000-000000000001', 'auth:log', '/auth/log/list');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-00000000000f', '删除', '', 1, '2022-05-14 14:10:14', 'button', '00000000-0000-0000-0000-00000000000e', 'auth:log:delete', '');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-000000000010', '清空', '', 2, '2022-05-14 14:10:14', 'button', '00000000-0000-0000-0000-00000000000e', 'auth:log:clear', '');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-000000000011', '详情', '', 3, '2022-05-14 14:10:14', 'button', '00000000-0000-0000-0000-00000000000e', 'auth:log:detail', '');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-000000000012', '异常列表', '', 5, '2022-05-14 14:10:14', 'menu', '00000000-0000-0000-0000-000000000001', 'exception:log:list', '/exception/log/list');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-000000000013', '删除', '', 3, '2022-05-14 14:10:14', 'button', '00000000-0000-0000-0000-000000000012', 'exception:log:delete', '');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-000000000015', '状态', '', 2, '2022-05-14 14:10:14', 'button', '00000000-0000-0000-0000-000000000012', 'exception:log:status', '');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-000000000014', '下载', '', 1, '2022-05-14 14:10:14', 'button', '00000000-0000-0000-0000-000000000012', 'exception:log:download', '');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-000000000001', '系统管理', 'Lock', 999, '2022-05-14 14:10:14', 'dir', NULL, '', '');
INSERT INTO "public"."a_menu" VALUES ('142d3f06-e1c1-11ec-bd8f-5f14373506b0', 'IP限制', '', 6, '2022-06-01 23:39:53', 'menu', '00000000-0000-0000-0000-000000000001', 'ip:limit:list', '/ip/limit/list');
INSERT INTO "public"."a_menu" VALUES ('1e0c1a0c-e1c1-11ec-bd8f-f5c89565c381', '删除', '', 1, '2022-06-01 23:40:09', 'button', '142d3f06-e1c1-11ec-bd8f-5f14373506b0', 'ip:limit:delete', '');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-000000000002', '账号列表', '', 1, '2022-05-14 14:10:14', 'menu', '00000000-0000-0000-0000-000000000001', 'auth:account', '/auth/account/list');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-000000000004', '新增', '', 1, '2022-05-14 14:10:14', 'button', '00000000-0000-0000-0000-000000000002', 'auth:account:add', '');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-000000000005', '编辑', '', 2, '2022-05-14 14:10:14', 'button', '00000000-0000-0000-0000-000000000002', 'auth:account:edit', '');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-000000000006', '重置密码', '', 3, '2022-05-14 14:10:14', 'button', '00000000-0000-0000-0000-000000000002', 'auth:account:reset', '');
INSERT INTO "public"."a_menu" VALUES ('00000000-0000-0000-0000-000000000007', '删除', '', 4, '2022-05-14 14:10:14', 'button', '00000000-0000-0000-0000-000000000002', 'auth:account:delete', '');
INSERT INTO "public"."a_menu" VALUES ('00643af7-d9b1-11ec-9309-b7ebdc95aa25', '状态', '', 5, '2022-05-22 17:24:38', 'button', '00000000-0000-0000-0000-000000000002', 'auth:account:status', '');

-- ----------------------------
-- Table structure for a_monitor
-- ----------------------------
DROP TABLE IF EXISTS "public"."a_monitor";
CREATE TABLE "public"."a_monitor" (
                                      "id" uuid NOT NULL,
                                      "server" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
                                      "create_time" timestamp(6) NOT NULL,
                                      "young_gc" int4 NOT NULL,
                                      "young_gc_time" int8 NOT NULL,
                                      "old_gc" int4 NOT NULL,
                                      "old_gc_time" int8 NOT NULL,
                                      "used_heap" int8 NOT NULL,
                                      "thread_count" int4 NOT NULL,
                                      "thread_daemon_count" int4 NOT NULL,
                                      "cpu_load_average" numeric(5,2) NOT NULL DEFAULT 0.0,
                                      "request_counter" int8 NOT NULL DEFAULT 0
)
;
COMMENT ON COLUMN "public"."a_monitor"."server" IS '服务器名称';
COMMENT ON COLUMN "public"."a_monitor"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."a_monitor"."young_gc" IS '年轻代GC次数';
COMMENT ON COLUMN "public"."a_monitor"."young_gc_time" IS '年轻代GC时间 ms';
COMMENT ON COLUMN "public"."a_monitor"."old_gc" IS '年老代GC次数';
COMMENT ON COLUMN "public"."a_monitor"."old_gc_time" IS '年老代GC时间 ms';
COMMENT ON COLUMN "public"."a_monitor"."used_heap" IS '已使用堆内存';
COMMENT ON COLUMN "public"."a_monitor"."thread_count" IS '线程数';
COMMENT ON COLUMN "public"."a_monitor"."thread_daemon_count" IS '守护线程数';
COMMENT ON COLUMN "public"."a_monitor"."cpu_load_average" IS 'CPU负载';
COMMENT ON COLUMN "public"."a_monitor"."request_counter" IS '请求次数';

-- ----------------------------
-- Records of a_monitor
-- ----------------------------

-- ----------------------------
-- Table structure for a_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."a_role";
CREATE TABLE "public"."a_role" (
                                   "id" uuid NOT NULL,
                                   "role_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                   "create_time" timestamp(6) NOT NULL
)
;
COMMENT ON COLUMN "public"."a_role"."role_name" IS '角色名';
COMMENT ON COLUMN "public"."a_role"."create_time" IS '创建时间';

-- ----------------------------
-- Records of a_role
-- ----------------------------
INSERT INTO "public"."a_role" VALUES ('00000000-0000-0000-0000-000000000000', '超级管理员', '2022-05-14 14:10:14.359596');

-- ----------------------------
-- Table structure for a_role_menu
-- ----------------------------
DROP TABLE IF EXISTS "public"."a_role_menu";
CREATE TABLE "public"."a_role_menu" (
                                        "id" uuid NOT NULL,
                                        "role_id" uuid NOT NULL,
                                        "menu_id" uuid NOT NULL
)
;
COMMENT ON COLUMN "public"."a_role_menu"."role_id" IS '角色ID';
COMMENT ON COLUMN "public"."a_role_menu"."menu_id" IS '菜单ID';

-- ----------------------------
-- Records of a_role_menu
-- ----------------------------
INSERT INTO "public"."a_role_menu" VALUES ('32485409-6fc3-11ed-b954-55c14c3c273f', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-000000000000');
INSERT INTO "public"."a_role_menu" VALUES ('324ac50a-6fc3-11ed-b954-d792166f7652', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-000000000001');
INSERT INTO "public"."a_role_menu" VALUES ('324d5d1b-6fc3-11ed-b954-a737c98c9af0', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-000000000002');
INSERT INTO "public"."a_role_menu" VALUES ('324fce1c-6fc3-11ed-b954-7dd1591faba3', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-000000000003');
INSERT INTO "public"."a_role_menu" VALUES ('3252662d-6fc3-11ed-b954-830dc6d6b1ee', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-000000000004');
INSERT INTO "public"."a_role_menu" VALUES ('3254fe3e-6fc3-11ed-b954-d10b02a4afe9', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-000000000005');
INSERT INTO "public"."a_role_menu" VALUES ('32576f3f-6fc3-11ed-b954-85bcaf50ff60', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-000000000006');
INSERT INTO "public"."a_role_menu" VALUES ('325a0750-6fc3-11ed-b954-77a87709b885', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-000000000007');
INSERT INTO "public"."a_role_menu" VALUES ('325c7851-6fc3-11ed-b954-6700eb8a5fb9', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-000000000008');
INSERT INTO "public"."a_role_menu" VALUES ('325ee952-6fc3-11ed-b954-4b7d7c8946aa', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-000000000009');
INSERT INTO "public"."a_role_menu" VALUES ('32618163-6fc3-11ed-b954-af9a00d86e49', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-00000000000a');
INSERT INTO "public"."a_role_menu" VALUES ('3263f264-6fc3-11ed-b954-b1b7b6feffa2', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-00000000000b');
INSERT INTO "public"."a_role_menu" VALUES ('32668a75-6fc3-11ed-b954-114f5c4a9b05', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-00000000000c');
INSERT INTO "public"."a_role_menu" VALUES ('3268fb76-6fc3-11ed-b954-c1295741d418', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-00000000000d');
INSERT INTO "public"."a_role_menu" VALUES ('326b6c77-6fc3-11ed-b954-7bf438aae777', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-00000000000e');
INSERT INTO "public"."a_role_menu" VALUES ('326e0488-6fc3-11ed-b954-2d31adcdf1c1', '00000000-0000-0000-0000-000000000000', '1e0c1a0c-e1c1-11ec-bd8f-f5c89565c381');
INSERT INTO "public"."a_role_menu" VALUES ('32704e79-6fc3-11ed-b954-11399d09892f', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-00000000000f');
INSERT INTO "public"."a_role_menu" VALUES ('3272e68a-6fc3-11ed-b954-dbcc4ce63050', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-000000000010');
INSERT INTO "public"."a_role_menu" VALUES ('3275578b-6fc3-11ed-b954-99d3b51d10d6', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-000000000011');
INSERT INTO "public"."a_role_menu" VALUES ('3277a17c-6fc3-11ed-b954-01b7594c4203', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-000000000012');
INSERT INTO "public"."a_role_menu" VALUES ('327a398d-6fc3-11ed-b954-47005dd5a745', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-000000000013');
INSERT INTO "public"."a_role_menu" VALUES ('327d1fbe-6fc3-11ed-b954-17544508bd38', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-000000000014');
INSERT INTO "public"."a_role_menu" VALUES ('327fdedf-6fc3-11ed-b954-1dcec2e191e6', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-000000000015');
INSERT INTO "public"."a_role_menu" VALUES ('328276f0-6fc3-11ed-b954-ab253c3a510e', '00000000-0000-0000-0000-000000000000', '142d3f06-e1c1-11ec-bd8f-5f14373506b0');
INSERT INTO "public"."a_role_menu" VALUES ('32855d21-6fc3-11ed-b954-4d889b292a45', '00000000-0000-0000-0000-000000000000', '00643af7-d9b1-11ec-9309-b7ebdc95aa25');

-- ----------------------------
-- Indexes structure for table a_account
-- ----------------------------
CREATE UNIQUE INDEX "a_account_name_idx" ON "public"."a_account" USING btree (
                                                                              "user_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table a_account
-- ----------------------------
ALTER TABLE "public"."a_account" ADD CONSTRAINT "a_admin_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table a_config
-- ----------------------------
CREATE UNIQUE INDEX "a_config_u_idx" ON "public"."a_config" USING btree (
                                                                         "config_key" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table a_config
-- ----------------------------
ALTER TABLE "public"."a_config" ADD CONSTRAINT "a_config_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table a_exception_log
-- ----------------------------
CREATE UNIQUE INDEX "a_exception_log_idx" ON "public"."a_exception_log" USING btree (
                                                                                     "msg_md5" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table a_exception_log
-- ----------------------------
ALTER TABLE "public"."a_exception_log" ADD CONSTRAINT "a_exception_log_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table a_exception_log_backup
-- ----------------------------
ALTER TABLE "public"."a_exception_log_backup" ADD CONSTRAINT "a_exception_log_backup_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table a_ip_limit
-- ----------------------------
ALTER TABLE "public"."a_ip_limit" ADD CONSTRAINT "a_ip_limit_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table a_log
-- ----------------------------
CREATE INDEX "a_log_desc_idx" ON "public"."a_log" USING btree (
                                                               "create_time" "pg_catalog"."timestamp_ops" DESC NULLS FIRST
    );

-- ----------------------------
-- Primary Key structure for table a_log
-- ----------------------------
ALTER TABLE "public"."a_log" ADD CONSTRAINT "a_log_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table a_menu
-- ----------------------------
ALTER TABLE "public"."a_menu" ADD CONSTRAINT "a_menu_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table a_monitor
-- ----------------------------
CREATE INDEX "a_monitor_idx" ON "public"."a_monitor" USING btree (
                                                                  "server" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
                                                                  "create_time" "pg_catalog"."timestamp_ops" DESC NULLS FIRST
    );

-- ----------------------------
-- Primary Key structure for table a_monitor
-- ----------------------------
ALTER TABLE "public"."a_monitor" ADD CONSTRAINT "a_monitor_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table a_role
-- ----------------------------
ALTER TABLE "public"."a_role" ADD CONSTRAINT "a_role_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table a_role_menu
-- ----------------------------
CREATE INDEX "role_menu_idx" ON "public"."a_role_menu" USING btree (
                                                                    "role_id" "pg_catalog"."uuid_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table a_role_menu
-- ----------------------------
ALTER TABLE "public"."a_role_menu" ADD CONSTRAINT "a_role_menu_pkey" PRIMARY KEY ("id");
