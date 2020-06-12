sample
===
* 注释

	select #use("cols")# from core_button  where  #use("condition")#

cols
===
	id,name,menuid,permission

updateSample
===
	
	id=#id#,name=#name#,menuid=#menuid#,permission=#permission#

condition
===

	1 = 1  
	@if(!isEmpty(id)){
	 and id=#id#
	@}
	@if(!isEmpty(name)){
	 and name=#name#
	@}
	@if(!isEmpty(menuid)){
	 and menuid=#menuid#
	@}
	@if(!isEmpty(permission)){
	 and permission=#permission#
	@}
	
	