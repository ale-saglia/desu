package dclient.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import dclient.model.Model;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainController {
	@FXML
	private TextField searchField;

	@FXML
	private TableView<RSPPtableElement> rsppTable;

	@FXML
	private TableColumn<RSPPtableElement, String> nameColumn;

	@FXML
	private TableColumn<RSPPtableElement, LocalDate> deadlineColumn;

	@FXML
	private TableColumn<RSPPtableElement, String> invoiceColumn;

	@FXML
	private TableColumn<RSPPtableElement, Boolean> payedColumn;

	@FXML
	private TableColumn<RSPPtableElement, String> noteColumn;

	@FXML
	private CheckBox checkBoxDeadline;

	@FXML
	private Button createNewButton;

	@FXML
	private Button viewEditButton;

	private ObservableList<RSPPtableElement> rsppElements;
	private FilteredList<RSPPtableElement> filteredrsppElements;
	private SortedList<RSPPtableElement> sortedFilteredrsppElements;

	Model model;

	public void createTable() {
		final List<Map<String, String>> datas = model.getDataForTable();
		rsppElements = FXCollections.observableArrayList();

		for (final Map<String, String> rsppElement : datas)
			rsppElements.add(new RSPPtableElement(rsppElement));

		filteredrsppElements = new FilteredList<RSPPtableElement>(rsppElements);

		nameColumn.setCellValueFactory(new PropertyValueFactory<RSPPtableElement, String>("accountName"));
		deadlineColumn.setCellValueFactory(cellData -> cellData.getValue().jobEndProperty());

		deadlineColumn.setCellFactory(column -> {
			return new TableCell<RSPPtableElement, LocalDate>() {
				@Override
				protected void updateItem(LocalDate item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
						setStyle("");
					} else {
						// Format date.
						setText(DateTimeFormatter.ofPattern(model.getConfig().getProperty("rsppTable.dateFormat"))
								.format(item));
					}
				}
			};
		});
		invoiceColumn.setCellValueFactory(new PropertyValueFactory<RSPPtableElement, String>("invoiceID"));

		payedColumn.setCellValueFactory(f -> f.getValue().payedProperty());
		payedColumn.setCellFactory(tc -> new CheckBoxTableCell<>());

		noteColumn.setCellValueFactory(new PropertyValueFactory<RSPPtableElement, String>("note"));

		System.out.println(searchField.getText());

		final ObjectProperty<Predicate<RSPPtableElement>> nameFilter = new SimpleObjectProperty<>();
		final ObjectProperty<Predicate<RSPPtableElement>> dateFilter = new SimpleObjectProperty<>();

		nameFilter.bind(Bindings.createObjectBinding(
				() -> rspp -> rspp.getAccountName().toLowerCase().contains(searchField.getText().toLowerCase()),
				searchField.textProperty()));

		dateFilter.bind(
				Bindings.createObjectBinding(() -> rspp -> dateFilter(rspp), checkBoxDeadline.selectedProperty()));

		filteredrsppElements.predicateProperty().bind(
				Bindings.createObjectBinding(() -> nameFilter.get().and(dateFilter.get()), nameFilter, dateFilter));

		sortedFilteredrsppElements = new SortedList<>(filteredrsppElements);
		rsppTable.setItems(sortedFilteredrsppElements);
		sortedFilteredrsppElements.comparatorProperty().bind(rsppTable.comparatorProperty());

		rsppTable.setRowFactory(tv -> {
			final TableRow<RSPPtableElement> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					switchToViewEdit();
				}
			});
			return row;
		});
	}

	private boolean dateFilter(final RSPPtableElement rspp) {
		if (checkBoxDeadline.isSelected()) {
			if (rspp.jobEndDate().isAfter(LocalDate.now()) && rspp.jobEndDate().isBefore(
					LocalDate.now().plusDays(Integer.parseInt(model.getConfig().getProperty("rsppTable.daysAdvance")))))
				return true;
			else
				return false;
		}
		return true;
	}

	public class RSPPtableElement {
		String jobID;
		LocalDate jobStart;
		ObjectProperty<LocalDate> jobEnd;
		StringProperty accountName;
		StringProperty category;
		StringProperty invoiceID;
		BooleanProperty payed;
		StringProperty note;

		public RSPPtableElement(final Map<String, String> rsspElement) {
			this.jobID = rsspElement.get("jobid");
			this.jobStart = LocalDate.parse(rsspElement.get("jobstart"));
			this.jobEnd = new SimpleObjectProperty<LocalDate>(LocalDate.parse(rsspElement.get("jobend")));
			this.accountName = new SimpleStringProperty(rsspElement.get("name"));
			this.note = new SimpleStringProperty(rsspElement.get("note"));
			this.category = new SimpleStringProperty(model.getAccountCategories().get(rsspElement.get("category")));
			this.invoiceID = new SimpleStringProperty(rsspElement.get("invoiceid"));
			this.payed = new SimpleBooleanProperty(rsspElement.get("payed").contains("true"));
		}

		public StringProperty accountNameProperty() {
			return accountName;
		}

		public String getAccountName() {
			return accountName.get();
		}

		public StringProperty categoryProperty() {
			return category;
		}

		public String getCategory() {
			return category.get();
		}

		public ObjectProperty<LocalDate> jobEndProperty() {
			return jobEnd;
		}

		public String getJobEnd() {
			return (jobEnd.get().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		}

		public LocalDate jobEndDate() {
			return jobEnd.get();
		}

		public StringProperty invoiceIDProperty() {
			return invoiceID;
		}

		public String getInvoiceID() {
			return invoiceID.get();
		}

		public BooleanProperty payedProperty() {
			return payed;
		}

		public Boolean getPayed() {
			return payed.get();
		}

		public String getJobID() {
			return jobID;
		}

		public LocalDate getJobStart() {
			return jobStart;
		}

		public StringProperty noteProperty() {
			return note;
		}

		public String getNote() {
			return note.get();
		}

	}

	public void setModel(final Model model) {
		this.model = model;
	}

	public void refresh() {
		model.refreshSession();
		createTable();
	}

	@FXML
	public void switchToViewEdit() {
		final RSPPtableElement selectedItems = rsppTable.getSelectionModel().getSelectedItem();
		if (selectedItems != null) {
			try {
				final Stage stage = new Stage();

				final FXMLLoader loader = new FXMLLoader(getClass().getResource("edit.fxml"));
				final VBox root = (VBox) loader.load();
				final ViewEditController controller = loader.getController();

				model.refreshSession();

				controller.setModel(model);
				controller.setCombo();
				controller.setRSPP(selectedItems.getJobID(), selectedItems.getJobStart());

				final Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				stage.setScene(scene);
				stage.show();
				stage.setOnCloseRequest((EventHandler<WindowEvent>) new EventHandler<WindowEvent>() {
					public void handle(WindowEvent we) {
						if (controller.isChanged) {
							System.out.println("Some elements were modified");
							refresh();
						}

						Platform.exit();
						System.exit(0);
					}
				});

			} catch (final Exception e) {
				e.printStackTrace();
			}
		} else {
			final Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Attenzione!");
			alert.setHeaderText("Riga RSPP non selezionata");
			alert.setContentText(
					"Come fare?\nPer utilizzare la funziona \"visualizza e modifica\" di questo programma occorre selezionare la riga della tabella e successivamente procedere cliccando il pulsante.");

			alert.showAndWait();
		}

	}
}