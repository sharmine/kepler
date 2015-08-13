/**
 * 基于滑屏的焦点图封装
 * 
 * @author heruijun
 */
!(function($) {

	var threetiSwipes = [];
	
	$.fn.threetiSwipe = function(options) {
		return this.each(function() {
			threetiSwipes.push(new $3ti(this, options));
		});
	};
	
	var defaults = {
		onLoad: null,	// 初始化完成回调
		screenWidth: $("body").width(), // 屏幕宽度，单张焦点图宽度
		currentImg: 0,
		maxImages: 0,
		speed: 500,
		pointers: '',
		playTime: 2000,
	};
	
	$.threetiSwipe = function(obj, options) {
		this.box_threetiSwipe = $(obj);
		this.settings = $.extend({}, defaults, options || {});
		this.settings.maxImages = this.box_threetiSwipe.find('img').length;
		this.setup();
	};
	
	var $3ti = $.threetiSwipe;
	$3ti.fn = $3ti.prototype = {};
	$3ti.fn.extend = $.extend;
	
	$3ti.fn.extend({
		
		/**
		 * 初始化
		 */
		setup: function(){
			var self = this;

			for ( var i = 0, max = this.settings.maxImages; i < max; i++) {
				this.settings.pointers += '<span class="pointer"></span>';
			}

			$('.pointers').html(this.settings.pointers).find('.pointer:first').addClass(
					'pointer-actived');
			
			this.box_threetiSwipe.width(this.settings.screenWidth * this.settings.maxImages);

			this.box_threetiSwipe.find('img').width(this.settings.screenWidth);
			
			this.box_threetiSwipe.swipe({
				triggerOnTouchEnd : true,
				swipeStatus : swipeStatus,
				allowPageScroll : "vertical",
				threshold : 75
			});
			
			var autoScroll;
			 
			// 在自动播放时调用的播放方法
			function beginPlay() {
				if(self.settings.currentImg < self.settings.maxImages - 1) {
					self.settings.currentImg ++;
					self.next();
				} else {
					self.scroll(0, 500);
					$('.pointers').html(self.settings.pointers).find('.pointer:first').addClass('pointer-actived');
					self.settings.currentImg = 0;
				}
			}
			
			// 自动播放开始
			function autoPlayStart() {
				autoPlayStop();
				autoScroll = setInterval(beginPlay, self.settings.playTime);
			}
			
			// 自动播放停止
			function autoPlayStop() {
				clearInterval(autoScroll);
			}

			// 手势状态识别
			function swipeStatus(event, phase, direction, distance) {
				if(phase == 'end' && distance == 0){
					var index = self.settings.currentImg;
					var url = self.box_threetiSwipe.find('img:eq('+ index +')').attr('linkTo');
					window.location.href = url;
				}

				if (phase == "move"
						&& (direction == "left" || direction == "right")) {
					var duration = 0;

					if (direction == "left") {
						self.scroll((self.settings.screenWidth * self.settings.currentImg) + distance,
								duration);
					} else if (direction == "right") {
						self.scroll((self.settings.screenWidth * self.settings.currentImg) - distance,
								duration);				
					}
					autoPlayStop();
				}

				else if (phase == "cancel") {
					self.scroll(self.settings.screenWidth * self.settings.currentImg, self.settings.speed);
					autoPlayStart();
				}

				else if (phase == "end") {
					if (direction == "right") {
						self.prev();
					} else if (direction == "left") {
						self.settings.currentImg ++;
						self.next();
					}
					autoPlayStart();
				}
			}
			if ($.isFunction(self.settings.onLoad)) {
				self.settings.onLoad(self);
			}
			autoPlayStart();
		},
		
		/**
		 * 上一张
		 */
		prev: function(){
			this.settings.currentImg = Math.max(this.settings.currentImg - 1, 0);
			this.scroll(this.settings.screenWidth * this.settings.currentImg, this.settings.speed);
		},
		
		/**
		 * 下一张
		 */
		next: function(){
			this.settings.currentImg = Math.min(this.settings.currentImg, this.settings.maxImages - 1);
			this.scroll(this.settings.screenWidth * this.settings.currentImg, this.settings.speed);
		},
		
		/**
		 * 滚动图片
		 * 
		 * @param distance 滚动或手势滑动的单位长度
		 * @param duration 持续时间
		 */
		scroll: function(distance, duration) {
			$('.pointers').find('.pointer').removeClass('pointer-actived');
			$('.pointers').find('.pointer').eq(this.settings.currentImg).addClass(
					'pointer-actived');
			
			this.box_threetiSwipe.css("-webkit-transition-duration", (duration / 1000)
					.toFixed(1)
					+ "s");
			this.box_threetiSwipe.css("-moz-transition-duration", (duration / 1000)
					.toFixed(1)
					+ "s");		

			var value = (distance < 0 ? "" : "-")
					+ Math.abs(distance).toString();

			this.box_threetiSwipe.css("-webkit-transform", "translate3d(" + value
					+ "px,0px,0px)");
			this.box_threetiSwipe.css("-moz-transform", "translate3d(" + value
					+ "px,0px,0px)");
		}
	});
})(jQuery);