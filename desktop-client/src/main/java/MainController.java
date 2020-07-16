import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import db.SicurteaDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Model;

public class MainController {
	@FXML
	private TextField searchField;

	@FXML
	private Button searchButton;

	@FXML
	private TableView<RSPPtableElement> rsppTable;

	@FXML
	private TableColumn<RSPPtableElement, String> nameColumn;

	@FXML
	private TableColumn<RSPPtableElement, String> categoryColumn;

	@FXML
	private TableColumn<RSPPtableElement, String> deadlineColumn;

	@FXML
	private TableColumn<RSPPtableElement, String> invoiceColumn;

	@FXML
	private TableColumn<RSPPtableElement, String> payedColumn;

	@FXML
	private CheckBox checkBoxDeadline;

	@FXML
	private Button viewEditButton;

	private ObservableList<RSPPtableElement> rsppElements;

	SicurteaDAO dao;
	Model model;

	@FXML
	public void initialize() {
		dao = new SicurteaDAO();

		rsppElements = FXCollections.observableArrayList();

		for (Map<String, String> rsppElement : dao.getDataForTable(false)) {
			rsppElements.add(new RSPPtableElement(rsppElement));
		}

		FilteredList<RSPPtableElement> filteredData = new FilteredList<>(rsppElements, p -> true);

		nameColumn.setCellValueFactory(new PropertyValueFactory<RSPPtableElement, String>("accountName"));
		categoryColumn.setCellValueFactory(new PropertyValueFactory<RSPPtableElement, String>("category"));
		deadlineColumn.setCellValueFactory(new PropertyValueFactory<RSPPtableElement, String>("jobEnd"));
		invoiceColumn.setCellValueFactory(new PropertyValueFactory<RSPPtableElement, String>("invoiceID"));
		payedColumn.setCellValueFactory(new PropertyValueFactory<RSPPtableElement, String>("payed"));

		searchButton.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(rspp -> {
				// If filter text is empty, display all persons.
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				// Compare first name and last name of every person with filter text.
				String lowerCaseFilter = newValue.toLowerCase();

				if (rspp.getAccountName().toLowerCase().contains(lowerCaseFilter)) {
					return true; // Filter matches first name.
				}
				return false; // Does not match.
			});
		});

		SortedList<RSPPtableElement> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(rsppTable.comparatorProperty());

		rsppTable.setItems(sortedData);
	}

	public class RSPPtableElement {
		String jobID;
		Date jobStart;

		Date jobEnd;
		StringProperty accountName;
		StringProperty category;
		StringProperty invoiceID;
		StringProperty payed;

		public RSPPtableElement(Map<String, String> rsspElement) {
			this.jobID = rsspElement.get("jobid");
			try {
				this.jobStart = new SimpleDateFormat("dd/MM/yyyy").parse(rsspElement.get("jobstart"));
				this.jobEnd = new SimpleDateFormat("dd/MM/yyyy").parse(rsspElement.get("jobend"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			this.accountName = new SimpleStringProperty(rsspElement.get("name"));
			switch (rsspElement.get("category")) {
			case "b2b":
				this.category = new SimpleStringProperty("Azienda");
				break;
			case "b2c":
				this.category = new SimpleStringProperty("Privato");
				break;
			case "pa":
				this.category = new SimpleStringProperty("Pubblica Amministrazione");
				break;
			default:
				throw new IllegalArgumentException();
			}

			this.invoiceID = new SimpleStringProperty(rsspElement.get("invoiceid"));

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
			return new SimpleStringProperty((new SimpleDateFormat("dd/MM/yyyy")).format(jobEnd));
		}

		public String getJobEnd() {
			return (new SimpleDateFormat("dd/MM/yyyy")).format(jobEnd);
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

	}

	@FXML
	public void search(ActionEvent event) {
		// TODO SETUP FILTER (explaination on
		// https://code.makery.ch/blog/javafx-8-tableview-sorting-filtering/ )
	}

	public void setModel(Model model) {
		this.model = model;
	}

	@FXML
	public void switchToViewEdit(ActionEvent event) throws Exception {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("edit.fxml"));
			Parent rootViewEdit = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.setScene(new Scene(rootViewEdit));
			stage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
