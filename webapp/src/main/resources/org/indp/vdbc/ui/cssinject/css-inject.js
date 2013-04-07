window.org_indp_vdbc_ui_cssinject_CssInject = function () {
    this.addStyle = function (elementId, css) {
        var styleElementId = "vdbc-cssinject-" + elementId;
        var styleElement = document.createElement("style");
        styleElement.type = "text/css";
        styleElement.id = styleElementId;
        document.getElementsByTagName("head")[0].appendChild(styleElement);

        var cssText = "." + styleElementId + "{" + css + "}";

        if (styleElement.styleSheet) {
            styleElement.styleSheet.cssText = cssText;
        }
        else {
            if (styleElement.firstChild) {
                styleElement.removeChild(styleElement.firstChild);
            }
            var rules = document.createTextNode(cssText);
            styleElement.appendChild(rules);
        }

        var element = document.getElementById(elementId);
        element.className += " " + styleElementId;
    }
};