package dss2526.ui.util;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

public class NewMenu {

    public enum MenuStyle {
        NUMBERED, LETTERED, BULLET("• "), DASH("- "), ARROW("> ");
        private final String prefix;
        MenuStyle() { this.prefix = null; }
        MenuStyle(String prefix) { this.prefix = prefix; }

        public String getPrefix(int i) {
            if (this == NUMBERED) return (i + 1) + ". ";
            if (this == LETTERED) return (char) ('A' + i) + ". ";
            return prefix;
        }
    }

    public enum BorderStyle {
        SIMPLE("====="), DOUBLE("═════"), ASTERISK("*****"), HASH("#####"), NONE("");
        private final String b;
        BorderStyle(String b) { this.b = b; }
        public void print(String t) {
            System.out.println(this == NONE ? "\n" + t : "\n" + b + " " + t + " " + b);
        }
    }

    @FunctionalInterface public interface Handler { boolean execute(); }

    private static final Scanner sc = new Scanner(System.in);
    private final String title;
    private final List<String> options;
    private final List<BooleanSupplier> preconditions;
    private final List<Handler> handlers;
    private MenuStyle style = MenuStyle.NUMBERED;
    private BorderStyle border = BorderStyle.SIMPLE;
    private boolean showLineNumbers = false;
    private String prompt = ">>> ";

    public NewMenu(String title, List<String> options) {
        this.title = title;
        this.options = new ArrayList<>(options);
        this.preconditions = new ArrayList<>(Collections.nCopies(options.size(), () -> true));
        this.handlers = new ArrayList<>(Collections.nCopies(options.size(), () -> {
            System.out.println("\nOpção não implementada!");
            return false;
        }));
    }

    public void registerHandlers(Handler... hs) { IntStream.range(0, Math.min(hs.length, handlers.size())).forEach(i -> handlers.set(i, hs[i])); }
    public void setHandler(int i, Handler h) { handlers.set(i - 1, h); }
    public void setMenuStyle(MenuStyle s) { this.style = s; }
    public void setBorderStyle(BorderStyle b) { this.border = b; }
    public void setPromptSymbol(String p) { this.prompt = p; }
    public void setShowLineNumbers(boolean show) { this.showLineNumbers = show; }

    public void run() {
        int choice;
        boolean exit = false;
        do {
            show();
            choice = readOption();
            if (choice > 0) {
                if (!preconditions.get(choice - 1).getAsBoolean()) System.out.println("\nOpção indisponível!");
                else exit = handlers.get(choice - 1).execute();
            }
        } while (choice != 0 && !exit);
    }

    private void show() {
        border.print(title);
        for (int i = 0; i < options.size(); i++) {
            // Se showLineNumbers estiver ativo e o estilo não for NUMBERED, prefixamos com o número
            String ln = (showLineNumbers && style != MenuStyle.NUMBERED) ? (i + 1) + ". " : "";
            String opt = preconditions.get(i).getAsBoolean() ? options.get(i) : "---";
            System.out.println(ln + style.getPrefix(i) + opt);
        }
        String lnExit = (showLineNumbers && style != MenuStyle.NUMBERED) ? "0. " : "";
        System.out.println(lnExit + "0. Sair");
    }

    private int readOption() {
        System.out.print(prompt);
        if (!sc.hasNextLine()) return 0;
        String line = sc.nextLine().trim();
        try {
            if (style == MenuStyle.LETTERED && !line.isEmpty()) {
                if (line.equals("0")) return 0;
                int val = Character.toUpperCase(line.charAt(0)) - 'A' + 1;
                return (val > 0 && val <= options.size()) ? val : -1;
            }
            int op = Integer.parseInt(line);
            return (op >= 0 && op <= options.size()) ? op : -1;
        } catch (Exception e) { return -1; }
    }

    public static Builder builder(String title) { return new Builder(title); }

    public static class Builder {
        private final String t;
        private final List<String> opts = new ArrayList<>();
        private final List<Handler> hnds = new ArrayList<>();
        private MenuStyle s = MenuStyle.NUMBERED;
        private BorderStyle b = BorderStyle.SIMPLE;
        private String p = ">>>> ";
        private boolean showLineNumbers = false;

        Builder(String title) { this.t = title; }
        public Builder addOption(String o, Handler h) { opts.add(o); hnds.add(h); return this; }
        public Builder style(MenuStyle s) { this.s = s; return this; }
        public Builder border(BorderStyle b) { this.b = b; return this; }
        public Builder showNumbers(boolean show) { this.showLineNumbers = show; return this; }
        
        public void run() {
            NewMenu m = new NewMenu(t, opts);
            m.setMenuStyle(s); 
            m.setBorderStyle(b); 
            m.setPromptSymbol(p);
            m.setShowLineNumbers(showLineNumbers); // Aplica a configuração ao menu
            m.registerHandlers(hnds.toArray(Handler[]::new));
            m.run();
        }
    }
}