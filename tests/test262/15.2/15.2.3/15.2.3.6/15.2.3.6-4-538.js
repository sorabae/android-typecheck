// TODO rewrite dataPropertyAttributesAreCorrect
// //   TODO getter/setter
// //   function testcase() 
// //   {
// //     var obj = {
// //       
// //     };
// //     var getFunc = (function () 
// //     {
// //       return 1001;
// //     });
// //     var verifySetFunc = "data";
// //     var setFunc = (function (value) 
// //     {
// //       verifySetFunc = value;
// //     });
// //     Object.defineProperty(obj, "prop", {
// //       get : getFunc,
// //       set : setFunc,
// //       enumerable : true,
// //       configurable : true
// //     });
// //     var desc1 = Object.getOwnPropertyDescriptor(obj, "prop");
// //     Object.defineProperty(obj, "prop", {
// //       value : 1001
// //     });
// //     var desc2 = Object.getOwnPropertyDescriptor(obj, "prop");
// //     return desc1.hasOwnProperty("get") && desc2.hasOwnProperty("value") && typeof desc2.get === "undefined" && typeof desc2.get === "undefined" && dataPropertyAttributesAreCorrect(obj, "prop", 1001, false, true, true);
// //   }
// //   {
// //     var __result1 = testcase();
// //     var __expect1 = true;
// //   }
// //   