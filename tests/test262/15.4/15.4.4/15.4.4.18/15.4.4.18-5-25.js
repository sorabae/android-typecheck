  function testcase() 
  {
    function innerObj() 
    {
      this._15_4_4_18_5_25 = true;
      var _15_4_4_18_5_25 = false;
      var result;
      function callbackfn(val, idx, obj) 
      {
        result = this._15_4_4_18_5_25;
      }
      var arr = [1, ];
      arr.forEach(callbackfn);
      this.retVal = ! result;
    }
    return new innerObj().retVal;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  