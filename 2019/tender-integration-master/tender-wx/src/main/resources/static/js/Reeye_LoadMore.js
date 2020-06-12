!function(a) {
    "use strict";
    let reeye = {
    	flag: true,	// 数据是否 未填充满滚动区
    	isNoMore: false,
    	loadingHtml: `<div id="loading">
            <div class="spinner">
              <div class="bounce1">加</div>
              <div class="bounce2">载</div>
              <div class="bounce3">中</div>
            </div>
        </div>`,
        noMoreHtml: `<div id="noMore">没有更多了</div>`,
    	init: function($ele, event) {
    		this.flag = true
            this.isNoMore = false
    		this.$ele = $ele
    		this.event = event
    		let that = this
    		this.$ele.unbind("scroll").bind("scroll", function(e) {
	    		that.duang(that.event)
	    	})
	    	this.duang(this.event)
    	},
	    loading: function() {
	    	if ($('#loading').length === 0)
	    		this.$ele.append(this.loadingHtml)
	    },
	    done: function() {
	    	$('#loading').remove()
	    	if (this.flag)
	    		this.duang(this.event)
	    },
	    noMore: function() {
            $('#loading').remove()
	    	this.isNoMore = true
            if ($('#noMore').length === 0)
	    	    this.$ele.append(this.noMoreHtml)
	    },
	    duang: function(doLoad) {
            if ($('#loading').length > 0) return
	    	if (!this.isNoMore) {
	    		let mainScrollHeight = this.$ele[0].scrollHeight,
		    		mainScrollTop = this.$ele.scrollTop(),
		    		childrenHeight = this.$ele.children('.child').length > 0 ? this.$ele.children('.child').last()[0].getBoundingClientRect().bottom : 0
		        if (childrenHeight <= this.$ele.height()) {	// 数据未填充满滚动区
		        	this.flag = childrenHeight !== this.$ele.height()
		        	this.loading()
		            doLoad(this)
		        }
	    	}
	    }
	}

    a.fn.loadMore = function(args) {
    	reeye.init($($(this)[0]), args.doLoad)
		return reeye
    }
}(window.jQuery)