$(function() {
  dhis2.contextmenu.makeContextMenu({
    menuId: 'contextMenu',
    menuItemActiveClass: 'contextMenuItemActive'
  });
});

function removeApprovalLevel( context ) {
    removeItem( context.id, context.name, i18n_confirm_delete_data_approval_level, 'removeApprovalLevel.action' );
}

function moveApprovalLevelUp( context ) {
    location.href = 'moveApprovalLevelUp.action?id=' + context.id;
}

function moveApprovalLevelDown( context ) {
    location.href = 'moveApprovalLevelDown.action?id=' + context.id;
}
