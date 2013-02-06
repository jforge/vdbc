window.org_indp_vaadinace_SourceCodeField = function () {
    var element = this.getElement();
    var rpcProxy = this.getRpcProxy();
    var editor = ace.edit(element);
    editor.setValue(' ');

    var self = this;

// todo uncomment after they fix http://dev.vaadin.com/ticket/10946
//    editor.getSession().on('change', function (e) {
//        rpcProxy.editorValueChanged(editor.getValue());
//    });

    this.onStateChange = function () {
        var state = this.getState();

        console.info("state change: ", state);

        var session = editor.getSession();
        session.setMode("ace/mode/" + state.language);
    };

    this.registerRpc({
        addCommand: function (id, bindKey) {
            console.info("addCommand: " + id);

            editor.commands.addCommand({
                name: id,
                bindKey: {win: bindKey},
                exec: function (editor) {

                    console.info("exec: " + id);

                    rpcProxy.executeCommand(id, self.createState())
                },
                readOnly: true
            });
        },

        setText: function (text) {
            editor.setValue(text);
        }
    });

    this.createState = function () {
        return {
            text: editor.getValue()
        };
    }
};
