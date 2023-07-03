/*
 * ====================================================================================
 *
 * Copyright (c) 2005, 2023 Oracle Ⓡ and/or its affiliates. All rights reserved.
 *
 * ====================================================================================
 */

package search;

import cluster.management.ServiceRegistry;
import model.Task;
import networking.OnRequestCallback;
import networking.WebClient;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SearchCoordinator implements OnRequestCallback {
  private static final String ENDPOINT = "/search";
  private static final String BOOKS_DIRECTORY = "./resources/book/";
  private final ServiceRegistry serviceRegistry;
  private final WebClient webClient;
  private final List<String> documents;

  public SearchCoordinator(ServiceRegistry workersServiceRegistry, WebClient webClient) {
    this.serviceRegistry = workersServiceRegistry;
    this.webClient = webClient;
    this.documents = readDocumentsList();
  }

  public List<Task> createTask(int numberOfWorkers, List<String> searchTerms) {
    List<List<String>> workersDocuments = splitDocumentList(numberOfWorkers, documents);
    List<Task> tasks = new ArrayList<>();

    for (List<String> documentsForWorker : workersDocuments) {
      Task task = new Task(searchTerms, documentsForWorker);
      tasks.add(task);
    }
    return tasks;
  }

  private static List<List<String>> splitDocumentList(int numberOfWorkers, List<String> documents) {
    int numberOfDocumentsPerWorker = (documents.size() + numberOfWorkers - 1) / numberOfWorkers;

    List<List<String>> workersDocuments = new ArrayList<>();

    for (int i = 0; i < numberOfWorkers; i++) {
      int firstDocumentIndex = i * numberOfDocumentsPerWorker;
      int lastDocumentIndexExclusive = Math.min(firstDocumentIndex + numberOfDocumentsPerWorker,
          documents.size());

      if (firstDocumentIndex >= lastDocumentIndexExclusive) {
        break;
      }
      List<String> currentWorkerDocuments = new ArrayList<>(documents.subList(firstDocumentIndex,
          lastDocumentIndexExclusive));

      workersDocuments.add(currentWorkerDocuments);
    }
    return workersDocuments;
  }
  private List<String> readDocumentsList() {
    File documentsDirectory = new File(BOOKS_DIRECTORY);
    return Arrays.stream(documentsDirectory.list())
        .map(documentName -> BOOKS_DIRECTORY + "/" + documentName)
        .collect(Collectors.toList());
  }

  @Override
  public byte[] handleRequest(byte[] requestPayload) {
    return new byte[0];
  }

  @Override
  public String getEndpoint() {
    return ENDPOINT;
  }
}
