package com.desertpetrol.GUI;

import com.desertpetrol.Main;
import com.desertpetrol.converter.Converter;
import com.desertpetrol.converter.Currency;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Locale;

//Main window. creates the GUI elements and their
@SuppressWarnings("CanBeFinal")
public class Window extends JFrame implements ActionListener {

        //Swing Elements
        JButton submit, reset;
        JFormattedTextField currencyFromInput, currencyToOutput;
        JLabel currencyFromLbl, currencyToLbl, currencyDateLbl;
        JComboBox<String> currencyToCombo;
        JComboBox<String> currencyFromCombo;
        JFrame frame;

        //Formatters and misc
        final String[] currencies = {"Real", "Dollar", "Euro", "Libras Esterlinas", "Peso argentino", "Peso Chileno"};
        Locale currencyLocale = new Locale("pt", "BR");

        public Window() throws IOException {
                frame = new JFrame();
                this.setTitle("Conversor de moedas");
                this.setDefaultCloseOperation(EXIT_ON_CLOSE);
                this.setResizable(false);
                this.setLayout(new FlowLayout(FlowLayout.LEFT, 50, 20));


                String imagePath = "/img/android-chrome-512x512.png";
                InputStream imgStream = Window.class.getResourceAsStream(imagePath );
                BufferedImage myImg = ImageIO.read(imgStream);
                this.setIconImage(myImg);

                //Painel contendo informações da conversão
                JPanel information = new JPanel();
                information.setLayout(new GridLayout(3, 1));
                this.add(information);
                currencyFromLbl = new JLabel("0 'BRL' igual a ");
                currencyFromLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
                information.add(currencyFromLbl);

                currencyToLbl = new JLabel("0.000000 'USD'");
                currencyToLbl.setFont(new Font("SansSerif", Font.PLAIN, 24));
                information.add(currencyToLbl);

                currencyDateLbl = new JLabel("AAAA-DD-MM 00:05 GMT");
                new Font("SansSerif", Font.BOLD, 12);
                information.add(currencyDateLbl);

                //Painel contendo os inputs da conversão
                GridLayout grid;
                JPanel inputs = new JPanel();
                inputs.setLayout(grid = new GridLayout(3, 1));
                grid.setHgap(12);
                grid.setVgap(8);
                this.add(inputs);
                currencyFromInput = new JFormattedTextField();
                inputs.add(currencyFromInput);

                currencyFromCombo = new JComboBox<>(currencies);
                currencyFromCombo.addActionListener(e -> checkComboSelection("From"));
                inputs.add(currencyFromCombo);

                currencyToOutput = new JFormattedTextField();
                currencyToOutput.setEditable(false);
                currencyToOutput.setText("$0.00");
                inputs.add(currencyToOutput);

                currencyToCombo = new JComboBox<>(currencies);
                currencyToCombo.setSelectedItem("Dollar");
                currencyToCombo.addActionListener(e -> checkComboSelection("to"));
                inputs.add(currencyToCombo);

                //Painel contendo os botões
                JPanel actionPanel = new JPanel();
                actionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                inputs.add(actionPanel);

                reset = new JButton("Limpar");
                reset.addActionListener(this);
                actionPanel.add(reset);

                submit = new JButton("Enviar");
                submit.addActionListener(this);
                actionPanel.add(submit);
                this.pack();
                this.setVisible(true);
        }

        public void checkComboSelection(String selectedBox) {
                JComboBox<String> selectedCombo = (selectedBox.equals("From")) ? currencyFromCombo : currencyToCombo;
                JComboBox<String> otherCombo = (selectedBox.equals("From")) ? currencyToCombo : currencyFromCombo;

                if (selectedCombo.getSelectedItem().equals(otherCombo.getSelectedItem())) {
                        String eM = "Você não pode converter uma moeda para ela mesma.";
                        JOptionPane.showMessageDialog(this, eM, "Error", JOptionPane.ERROR_MESSAGE);
                        selectedCombo.setSelectedIndex(0);
                }
        }

        public void selectCurrencyFromComboBox(JComboBox<String> comboBox, int conversionPriority) {
                String currencyCode = null;

                switch ((String) comboBox.getSelectedItem()) {
                        case "Dollar":
                                currencyCode = "USD";
                                currencyLocale = Locale.US;
                                update();
                                break;
                        case "Real":
                                currencyCode = "BRL";
                                currencyLocale = new Locale("pt", "BR");
                                break;
                        case "Euro":
                                currencyCode = "EUR";
                                currencyLocale = Locale.GERMANY;
                                break;
                        case "Libras Esterlinas":
                                currencyCode = "GBP";
                                currencyLocale = Locale.UK;
                                break;
                        case "Peso argentino":
                                currencyCode = "ARS";
                                currencyLocale = new Locale("es", "AR");
                                break;
                        case "Peso Chileno":
                                currencyCode = "CLP";
                                currencyLocale = new Locale("es", "CL");
                                break;
                        default:
                                break;
                }

                if (conversionPriority < 1) {
                        Currency.setFromCode(currencyCode);
                } else {
                        Currency.setToCode(currencyCode);
                }
        }

        public void submitRequest() {
                try {
                        selectCurrencyFromComboBox(currencyFromCombo, 0);
                        selectCurrencyFromComboBox(currencyToCombo, 1);
                        Currency.setAmount(Double.parseDouble(currencyFromInput.getText()));
                        Converter.handleConversion();
                        update();
                }
                catch (NumberFormatException e) {
                        final String eM = """
                                Valor inválido.\s
                                Evite letras e divida as casas decimais com ponto final.\s
                                Ex: 4.50""";
                        JOptionPane.showMessageDialog(this, eM, "Error", JOptionPane.ERROR_MESSAGE);
                }
        }

        public void clearRequest() {
                Currency.setFromCode("___");
                Currency.setToCode("___");
                Converter.setConversionRate(0.000000);
                Converter.setConversionDate("AAAA-DD-MM");
                currencyFromInput.setText("");
                Converter.setConversionResult(0);
                update();
        }

        public void update() {
                currencyFromLbl.setText("1 " + Currency.getFromCode() + " igual a ");
                currencyToLbl.setText(Converter.getConversionRate() + " " + Currency.getToCode() + " ");
                currencyDateLbl.setText(Converter.getConversionDate() + " 00:05 GMT");
                currencyToOutput.setText(formatCurrency(Converter.getConversionResult(), currencyLocale));
        }

        public String formatCurrency(double value, Locale locale) {
                NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
                return formatter.format(value);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
                if (e.getSource() == submit) { submitRequest(); }
                else
                if (e.getSource() == reset)  { clearRequest();  }
        }
}

