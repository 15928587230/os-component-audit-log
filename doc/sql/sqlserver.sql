CREATE TABLE [dbo].[t_system_log] (
  [id] bigint  NOT NULL,
  [content] nvarchar(2000) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [create_time] datetime2(0)  NOT NULL,
  [user_id] nvarchar(64) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [user_name] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [source] nvarchar(32) COLLATE Chinese_PRC_CI_AS  NULL,
  [module_name] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [url] nvarchar(512) COLLATE Chinese_PRC_CI_AS  NULL,
  [error] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [ip] nvarchar(32) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [type] nvarchar(20) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [detail] nvarchar(max) COLLATE Chinese_PRC_CI_AS  NULL,
  [spare1] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [spare2] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  [spare3] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
  CONSTRAINT [PK__t_system__3213E83F6189A282] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
)
ON [PRIMARY]
TEXTIMAGE_ON [PRIMARY]
GO

ALTER TABLE [dbo].[t_system_log] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'id',
'SCHEMA', N'dbo',
'TABLE', N't_system_log',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'操作内容',
'SCHEMA', N'dbo',
'TABLE', N't_system_log',
'COLUMN', N'content'
GO

EXEC sp_addextendedproperty
'MS_Description', N'操作时间',
'SCHEMA', N'dbo',
'TABLE', N't_system_log',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'操作人id',
'SCHEMA', N'dbo',
'TABLE', N't_system_log',
'COLUMN', N'user_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'操作人',
'SCHEMA', N'dbo',
'TABLE', N't_system_log',
'COLUMN', N'user_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'来源系统',
'SCHEMA', N'dbo',
'TABLE', N't_system_log',
'COLUMN', N'source'
GO

EXEC sp_addextendedproperty
'MS_Description', N'模块名',
'SCHEMA', N'dbo',
'TABLE', N't_system_log',
'COLUMN', N'module_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'请求接口url',
'SCHEMA', N'dbo',
'TABLE', N't_system_log',
'COLUMN', N'url'
GO

EXEC sp_addextendedproperty
'MS_Description', N'错误信息',
'SCHEMA', N'dbo',
'TABLE', N't_system_log',
'COLUMN', N'error'
GO

EXEC sp_addextendedproperty
'MS_Description', N'访问IP',
'SCHEMA', N'dbo',
'TABLE', N't_system_log',
'COLUMN', N'ip'
GO

EXEC sp_addextendedproperty
'MS_Description', N'操作类型',
'SCHEMA', N'dbo',
'TABLE', N't_system_log',
'COLUMN', N'type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'详情 可选',
'SCHEMA', N'dbo',
'TABLE', N't_system_log',
'COLUMN', N'detail'
GO

EXEC sp_addextendedproperty
'MS_Description', N'备用1',
'SCHEMA', N'dbo',
'TABLE', N't_system_log',
'COLUMN', N'spare1'
GO

EXEC sp_addextendedproperty
'MS_Description', N'备用2',
'SCHEMA', N'dbo',
'TABLE', N't_system_log',
'COLUMN', N'spare2'
GO

EXEC sp_addextendedproperty
'MS_Description', N'备用3',
'SCHEMA', N'dbo',
'TABLE', N't_system_log',
'COLUMN', N'spare3'
