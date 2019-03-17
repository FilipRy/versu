'use strict';

describe('myApp.view2 module', function() {

  beforeEach(module('myApp.post_detail'));

  describe('view2 controller', function(){

    it('should ....', inject(function($controller) {
      //spec body
      var view2Ctrl = $controller('postDetailCtrl');
      expect(view2Ctrl).toBeDefined();
    }));

  });
});