sample
===
* 注释

	select #use("cols")# from ST_SHIPIN_B  where  #use("condition")#

cols
===
	ID,SSZZ,NAME,VIDEO_ID,SWZ,SWZ_ID,TURN,PASS,SHOW,E,N,NEWVIDEO_ID,SLEVEL

updateSample
===
	
	ID=#id#,SSZZ=#sszz#,NAME=#name#,VIDEO_ID=#videoId#,SWZ=#swz#,SWZ_ID=#swzId#,TURN=#turn#,PASS=#pass#,SHOW=#show#,E=#e#,N=#n#,NEWVIDEO_ID=#newvideoId#,SLEVEL=#slevel#
deleteSample
===
       DELETE FROM HWSWJ.ST_SHIPIN_B WHERE #use("condition")#
       
condition
===

	1 = 1  
	@if(!isEmpty(id)){
	 and ID=#id#
	@}
	@if(!isEmpty(sszz)){
	 and SSZZ=#sszz#
	@}
	@if(!isEmpty(name)){
	 and NAME=#name#
	@}
	@if(!isEmpty(videoId)){
	 and VIDEO_ID=#videoId#
	@}
	@if(!isEmpty(swz)){
	 and SWZ=#swz#
	@}
	@if(!isEmpty(swzId)){
	 and SWZ_ID=#swzId#
	@}
	@if(!isEmpty(turn)){
	 and TURN=#turn#
	@}
	@if(!isEmpty(pass)){
	 and PASS=#pass#
	@}
	@if(!isEmpty(show)){
	 and SHOW=#show#
	@}
	@if(!isEmpty(e)){
	 and E=#e#
	@}
	@if(!isEmpty(n)){
	 and N=#n#
	@}
	@if(!isEmpty(newvideoId)){
	 and NEWVIDEO_ID=#newvideoId#
	@}
	@if(!isEmpty(slevel)){
	 and SLEVEL=#slevel#
	@}
	
	