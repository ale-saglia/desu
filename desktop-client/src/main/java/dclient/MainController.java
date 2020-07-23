package dclient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import dclient.model.Model;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController {
	// Change this to change deadlines checkbox behavior (set interval between today
	// and DAYS_ADVANCE days from now
	public final long DAYS_ADVANCE = 14;

	@FXML
	private TextField searchField;

	@FXML
	private TableView<RSPPtableElement> rsppTable;

	@FXML
	private TableColumn<RSPPtableElement, String> nameColumn;

	@FXML
	private TableColumn<RSPPtableElement, String> deadlineColumn;

	@FXML
	private TableColumn<RSPPtableElement, String> invoiceColumn;

	@FXML
	private TableColumn<RSPPtableElement, String> payedColumn;
	
	@FXML
	private TableColumn<RSPPtableElement, String> noteColumn;

	@FXML
	private CheckBox checkBoxDeadline;

	@FXML
	private Button viewEditButton;

	private ObservableList<RSPPtableElement> rsppElements;
	private FilteredList<RSPPtableElement> filteredrsppElements;
	private SortedList<RSPPtableElement> SortedFilteredrsppElements;

	Model model;

	public void createTable() {
		final List<Map<String, String>> datas = model.getDataForTable();
		rsppElements = FXCollections.observableArrayList();

		for (final Map<String, String> rsppElement : datas) rsppElements.add(new RSPPtableElement(rsppElement));

		filteredrsppElements = new FilteredList<RSPPtableElement>(rsppElements);

		nameColumn.setCellValueFactory(new PropertyValueFactory<RSPPtableElement, String>("accountName"));
		deadlineColumn.setCellValueFactory(new PropertyValueFactory<RSPPtableElement, String>("jobEnd"));
		invoiceColumn.setCellValueFactory(new PropertyValueFactory<RSPPtableElement, String>("invoiceID"));
		payedColumn.setCellValueFactory(new PropertyValueFactory<RSPPtableElement, String>("payed"));
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

		SortedFilteredrsppElements = new SortedList<>(filteredrsppElements);

		rsppTable.setItems(SortedFilteredrsppElements);

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
			if (rspp.jobEndDate().isAfter(LocalDate.now())
					&& rspp.jobEndDate().isBefore(LocalDate.now().plusDays(DAYS_ADVANCE)))
				return true;
			else
				return false;
		}
		return true;
	}

	public class RSPPtableElement {
		DateTimeFormatter formatter;

		String jobID;
		LocalDate jobStart;
		
		//TODO change this to use Localdate instead of string to sort in tableview
		LocalDate jobEnd;
		StringProperty accountName;
		StringProperty category;
		StringProperty invoiceID;
		StringProperty payed;
		StringProperty note;

		public RSPPtableElement(final Map<String, String> rsspElement) {
			formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			this.jobID = rsspElement.get("jobid");
			this.jobStart = LocalDate.parse(rsspElement.get("jobstart"), formatter);
			this.jobEnd = LocalDate.parse(rsspElement.get("jobend"), formatter);
			this.accountName = new SimpleStringProperty(rsspElement.get("name"));
			this.note = new SimpleStringProperty(rsspElement.get("note"));
			this.category = new SimpleStringProperty(model.getAccountCategories().get(rsspElement.get("category")));
			this.invoiceID = new SimpleStringProperty(rsspElement.get("invoiceid"));

			
			//TODO change this to use checkbox instead of ascii char
			if (rsspElement.get("payed") == "true")
				payed = new SimpleStringProperty("✔");
			else
				payed = new SimpleStringProperty("");
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

		public StringProperty jobEndProperty() {
			return new SimpleStringProperty(jobEnd.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		}

		public String getJobEnd() {
			return (jobEnd.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		}

		public LocalDate jobEndDate() {
			return jobEnd;
		}

		public StringProperty invoiceIDProperty() {
			return invoiceID;
		}

		public String getInvoiceID() {
			return invoiceID.get();
		}

		public StringProperty payedProperty() {
			return payed;
		}

		public String getPayed() {
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
		// Model.refreshSession();
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