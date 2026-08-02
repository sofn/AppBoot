-- V9: 菜单调整：删除 /global 首页；任务管理改名为 示例：任务管理 并移动到系统监控下

DELETE FROM sys_menu WHERE menu_name = '首页' AND path = '/global';

UPDATE sys_menu
SET menu_name = '示例：任务管理',
    parent_id = 2,
    remark = '示例：任务管理菜单',
    meta_info = '{"title":"示例：任务管理","icon":"ep:list","showParent":true,"rank":4}',
    update_time = CURRENT_TIMESTAMP
WHERE menu_id = 70;
